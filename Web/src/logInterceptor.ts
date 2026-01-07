// src/utils/logInterceptor.ts

type LogType = 'log' | 'warn' | 'error' | 'debug';

interface LogEntry {
    type: LogType;
    messages: any[];
    timestamp: number;
}

// A simple in-memory store for logs and a way to notify listeners
const logStore: LogEntry[] = [];
const listeners: Set<(log: LogEntry) => void> = new Set();
const MAX_LOGS = 100; // Limit the number of logs to prevent excessive memory use

// Capture original console methods once
const originalConsole = {
    log: console.log.bind(console),
    warn: console.warn.bind(console),
    error: console.error.bind(console),
    debug: console.debug ? console.debug.bind(console) : console.log.bind(console),
};

// Function to intercept and store logs
const interceptLog = (type: LogType, ...messages: any[]) => {
    const logEntry: LogEntry = {
        type,
        messages,
        timestamp: Date.now(),
    };

    logStore.push(logEntry);
    if (logStore.length > MAX_LOGS) {
        logStore.shift(); // Remove oldest log if capacity exceeded
    }

    // Notify all active listeners
    listeners.forEach(listener => listener(logEntry));

    // Still call the original console method
    originalConsole[type](...messages);
};

export const startConsoleInterception = () => {
    // Only intercept if not already intercepted
    if ((window.console as any).__intercepted__) {
        return;
    }

    (window.console as any).__intercepted__ = true;

    console.log = (...args: any[]) => interceptLog('log', ...args);
    console.warn = (...args: any[]) => interceptLog('warn', ...args);
    console.error = (...args: any[]) => interceptLog('error', ...args);
    console.debug = (...args: any[]) => interceptLog('debug', ...args);
};

export const stopConsoleInterception = () => {
    if (!(window.console as any).__intercepted__) {
        return;
    }
    console.log = originalConsole.log;
    console.warn = originalConsole.warn;
    console.error = originalConsole.error;
    console.debug = originalConsole.debug;
    (window.console as any).__intercepted__ = false;
};


// Functions for React component to subscribe/unsubscribe
export const subscribeToLogs = (callback: (log: LogEntry) => void) => {
    listeners.add(callback);
    // Optionally, send existing logs to the new subscriber immediately
    logStore.forEach(log => callback(log));
};

export const unsubscribeFromLogs = (callback: (log: LogEntry) => void) => {
    listeners.delete(callback);
};

export const getInitialLogs = (): LogEntry[] => {
    return [...logStore]; // Return a copy
};