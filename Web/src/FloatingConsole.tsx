// src/FloatingConsole.tsx (renamed from App.tsx example)
import { useEffect, useRef, useState } from 'react';
import { getInitialLogs, subscribeToLogs, unsubscribeFromLogs } from './logInterceptor';

interface LogEntry {
    type: 'log' | 'warn' | 'error' | 'debug';
    messages: any[];
    timestamp: number;
}
const MAX_DISPLAYED_LOGS = 50; // Or 100, depending on your UI needs

function FloatingConsole() {
    const containerRef = useRef<HTMLDivElement>(null);
    const [logs, setLogs] = useState<LogEntry[]>(getInitialLogs()); // Get any logs that happened before mount

    useEffect(() => {
        const handleNewLog = (newLog: LogEntry) => {
            setLogs(prevLogs => {
                const updatedLogs = [...prevLogs, newLog];
                // Keep only the most recent MAX_DISPLAYED_LOGS
                if (updatedLogs.length > MAX_DISPLAYED_LOGS) {
                    return updatedLogs.slice(updatedLogs.length - MAX_DISPLAYED_LOGS);
                }
                return updatedLogs;
            });
        };

        subscribeToLogs(handleNewLog);
        return () => {
            unsubscribeFromLogs(handleNewLog);
        };
    }, []);

    // Effect to scroll to bottom whenever logs change
    useEffect(() => {
        if (containerRef.current) {
            containerRef.current.scrollTop = containerRef.current.scrollHeight;
        }
    }, [logs]); // Re-run when logs array changes

    // Helper to safely format messages
    const formatMessages = (messages: any[]): string => {
        return messages.map(arg => {
            if (typeof arg === 'object' && arg !== null) {
                try {
                    return JSON.stringify(arg, null, 2); // Pretty print objects
                } catch (e) {
                    return '[Object (circular/unserializable)]';
                }
            }
            return String(arg);
        }).join(' ');
    };

    const getColor = (type: LogEntry['type']) => {
        switch (type) {
            case 'error': return '#f66';
            case 'warn': return '#ff0';
            case 'debug': return '#66f';
            default: return '#0f0';
        }
    };


    return (
        <div
            ref={containerRef}
            style={{
                position: 'fixed',
                top: 10,
                right: 10,
                width: 1920,
                maxHeight: 400,
                overflowY: 'auto',
                background: 'rgba(0, 0, 0, 0.85)',
                fontFamily: 'monospace',
                fontSize: 12,
                padding: 10,
                borderRadius: 8,
                zIndex: 9999,
                boxShadow: '0 0 10px #000',
                pointerEvents: 'none', // Prevent interference with other elements
                color: '#fff', // Default text color
            }}
        >
            {logs.map((log, index) => (
                <div key={index} style={{ color: getColor(log.type), marginBottom: 2 }}>
                    <span style={{ fontWeight: 'bold' }}>[{log.type.toUpperCase()}]</span> {formatMessages(log.messages)}
                </div>
            ))}
        </div>
    );
}

export default FloatingConsole;