# ViewQueryEngine Documentation & HTML Analysis Guide

## Overview

This document provides comprehensive documentation of the HopprTV SDK ViewQueryEngine system. It serves as a reference for future Claude instances to analyze any HTML file and provide accurate query instructions for element selection.

## Table of Contents

1. [ViewQueryEngine System Reference](#viewqueryengine-system-reference)
2. [Query Methods Documentation](#query-methods-documentation)
3. [Extraction Functions Documentation](#extraction-functions-documentation)
4. [HTML Analysis Guidelines](#html-analysis-guidelines)
5. [Query Construction Examples](#query-construction-examples)
6. [Future Claude Instructions](#future-claude-instructions)

---

## ViewQueryEngine System Reference

### Core Architecture

The ViewQueryEngine system consists of:

1. **ViewQueryEngine** (`ViewQueryEngine.ts`) - Main entry point
2. **QueryParser** (`QueryParser.ts`) - JSON query configuration parser
3. **QueryExecutorFactory** (`executors/QueryExecutorFactory.ts`) - Query method dispatcher
4. **ExtractorFactory** (`extractors/ExtractorFactory.ts`) - Data extraction dispatcher
5. **Types** (`types.ts`) - Interface definitions

### Main Entry Point

```typescript
ViewQueryEngine.execute(queryInput: string, extractionFunction: ExtractionFunction): ExtractionResult
```

**Parameters:**
- `queryInput`: JSON string containing query configuration
- `extractionFunction`: Function to extract data from found elements

**Return Type:**
```typescript
interface ExtractionResult {
    value: any;
    success: boolean;
    error?: string;
}
```

### Query Configuration Format

```json
{
    "query": "selector or search term",
    "method": "query method name",
    "attribute": "optional attribute name"
}
```

**Alternative formats supported:**
- `queryMethod` instead of `method`
- `attributeName` instead of `attribute`

---

## Query Methods Documentation

### 1. QuerySelector Method (`queryselector`)

**Description:** Uses standard CSS selectors via `document.querySelectorAll()`

**Usage:**
```json
{"query": "#main-container", "method": "queryselector"}
{"query": ".btn.btn-primary", "method": "queryselector"}
{"query": "div > h1", "method": "queryselector"}
```

**Implementation:** `QuerySelectorExecutor.ts`

### 2. XPath Method (`xpath`)

**Description:** Uses XPath expressions via `document.evaluate()`

**Usage:**
```json
{"query": "//div[@id='main-container']", "method": "xpath"}
{"query": "//button[contains(@class, 'btn')]", "method": "xpath"}
```

**Implementation:** `XPathExecutor.ts`

### 3. ID Method (`id`)

**Description:** Finds element by ID using `document.getElementById()`

**Usage:**
```json
{"query": "main-container", "method": "id"}
{"query": "submit-btn", "method": "id"}
```

**Implementation:** `DOMExecutor.executeById()`

### 4. Class Methods (`class`, `classname`)

**Description:** Finds elements by class name using `document.getElementsByClassName()`

**Usage:**
```json
{"query": "btn", "method": "class"}
{"query": "form-input", "method": "classname"}
```

**Implementation:** `DOMExecutor.executeByClassName()`

### 5. Tag Methods (`tag`, `tagname`)

**Description:** Finds elements by tag name using `document.getElementsByTagName()`

**Usage:**
```json
{"query": "button", "method": "tag"}
{"query": "img", "method": "tagname"}
```

**Implementation:** `DOMExecutor.executeByTagName()`

### 6. Name Method (`name`)

**Description:** Finds elements by name attribute using `document.getElementsByName()`

**Usage:**
```json
{"query": "username", "method": "name"}
{"query": "country", "method": "name"}
```

**Implementation:** `DOMExecutor.executeByName()`

### 7. Text Method (`text`)

**Description:** Finds elements by exact text content using XPath

**Usage:**
```json
{"query": "Main Title", "method": "text"}
{"query": "Submit Form", "method": "text"}
```

**Implementation:** `TextExecutor.executeByExactText()` - uses XPath: `//*[normalize-space(text())="${text}"]`

### 8. Partial Text Method (`partialtext`)

**Description:** Finds elements containing specified text using XPath

**Usage:**
```json
{"query": "Submit", "method": "partialtext"}
{"query": "Screen", "method": "partialtext"}
```

**Implementation:** `TextExecutor.executeByPartialText()` - uses XPath: `//*[contains(normalize-space(text()), "${text}")]`

### 9. Attribute Method (`attribute`)

**Description:** Finds elements by attribute presence or value

**Usage:**
```json
{"query": "data-focused", "method": "attribute"}
{"query": "data-focused=true", "method": "attribute"}
{"query": "data-testid*=main", "method": "attribute"}
```

**Supported Patterns:**
- `attribute` - Elements with attribute
- `attribute=value` - Elements with exact attribute value
- `attribute*=value` - Elements with attribute containing value

**Implementation:** `AttributeExecutor.ts` - converts to XPath

### 10. TreeWalker Method (`treewalker`)

**Description:** Uses TreeWalker API to traverse DOM with filtering

**Usage:**
```json
{"query": "tagname=button", "method": "treewalker"}
{"query": "classname=btn", "method": "treewalker"}
{"query": "id=main-container", "method": "treewalker"}
```

**Supported Filters:**
- `tagname=value`
- `classname=value`
- `id=value`

**Implementation:** `TreeWalkerExecutor.ts`

---

## Extraction Functions Documentation

### Existence Functions

#### `exists`
**Returns:** `boolean` - Whether any elements were found
**Usage:** Check if elements exist
```json
{"query": "#main-container", "method": "queryselector"}
// With extractionFunction: "exists" -> true/false
```

#### `count`
**Returns:** `number` - Number of elements found
**Usage:** Count matching elements
```json
{"query": "button", "method": "tag"}
// With extractionFunction: "count" -> 3
```

#### `isConnected`
**Returns:** `boolean` - Whether any element is connected to DOM
**Usage:** Check if elements are still in DOM

### Content Functions

#### `innerHTML`
**Returns:** `string[]` - Inner HTML of elements
**Usage:** Get HTML content inside elements

#### `outerHTML`
**Returns:** `string[]` - Outer HTML of elements
**Usage:** Get complete HTML including element tags

#### `textContent`
**Returns:** `string[]` - Text content of elements
**Usage:** Get all text content including hidden elements

#### `innerText`
**Returns:** `string[]` - Visible text content of elements
**Usage:** Get visible text content only

### Element Info Functions

#### `id`
**Returns:** `string[]` - ID attributes of elements

#### `className`
**Returns:** `string[]` - Class name strings of elements

#### `classList`
**Returns:** `string[][]` - Arrays of individual class names

#### `tagName`
**Returns:** `string[]` - Tag names of elements

#### `nodeName`
**Returns:** `string[]` - Node names of elements

#### `localName`
**Returns:** `string[]` - Local names of elements

#### `nodeType`
**Returns:** `number[]` - Node types of elements

#### `nodeValue`
**Returns:** `any[]` - Node values of elements

### Attribute Functions

#### `attribute`
**Returns:** `string[]` - Values of specified attribute
**Requires:** `attribute` parameter in query config
```json
{"query": "button", "method": "tag", "attribute": "data-focused"}
// With extractionFunction: "attribute" -> ["true", null, null]
```

#### `attributes`
**Returns:** `object[]` - All attributes as key-value objects

#### `hasAttribute`
**Returns:** `boolean[]` - Whether elements have specified attribute
**Requires:** `attribute` parameter

### Style Functions

#### `computedStyle`
**Returns:** `string[]` - Computed CSS property values
**Requires:** `attribute` parameter with CSS property name
```json
{"query": "#main-container", "method": "id", "attribute": "display"}
// With extractionFunction: "computedStyle" -> ["block"]
```

#### `style`
**Returns:** `string[]` - Inline style property values
**Without attribute:** Returns complete cssText
**With attribute:** Returns specific property value

#### `isVisible`
**Returns:** `boolean[]` - Whether elements are visible
**Checks:** display, visibility, opacity, dimensions

### Position Functions

#### `getBoundingClientRect`
**Returns:** `object[]` - Bounding rectangle objects
**Object properties:** x, y, width, height, top, left, bottom, right

#### `offsetWidth`, `offsetHeight`, `offsetTop`, `offsetLeft`
**Returns:** `number[]` - Offset dimensions and positions

#### `scrollTop`, `scrollLeft`, `scrollWidth`, `scrollHeight`
**Returns:** `number[]` - Scroll positions and dimensions

### Form Functions

#### `value`
**Returns:** `any[]` - Form element values

#### `checked`
**Returns:** `boolean[]` - Checkbox/radio checked states

#### `selected`
**Returns:** `boolean[]` - Option selected states

#### `disabled`
**Returns:** `boolean[]` - Form element disabled states

#### `hidden`
**Returns:** `boolean[]` - Element hidden states

### Structure Functions

#### `childElementCount`
**Returns:** `number[]` - Number of child elements

#### `childNodes`
**Returns:** `number[]` - Number of child nodes

#### `children`
**Returns:** `number[]` - Number of child elements

#### `parentElement`
**Returns:** `string[]` - Parent element tag names

#### `nextElementSibling`
**Returns:** `string[]` - Next sibling tag names

#### `previousElementSibling`
**Returns:** `string[]` - Previous sibling tag names

### Property Function

#### `property`
**Returns:** `any[]` - Custom property values
**Requires:** `attribute` parameter with property path
**Supports:** Dot notation for nested properties
**Example:** `"property"` with attribute `"dataset.testid"`

---

## HTML Analysis Guidelines

### Step 1: Analyze HTML Structure

When provided with an HTML file, first identify:

**Core Structure Elements:**
- Root containers and IDs
- Navigation elements
- Content sections and layouts
- Form elements
- Interactive elements (buttons, links)

**Attribute Patterns:**
- `id` attributes for unique identification
- `class` attributes for styling and grouping
- `data-*` attributes for application data
- `name` attributes for form elements
- Custom attributes

**Content Types:**
- Text content and headings
- Lists and tables
- Form inputs and controls
- Images and media
- Dynamic content areas

### Step 2: Identify Selection Strategies

**For Unique Elements:**
- Use `id` method for elements with unique IDs
- Use specific CSS selectors with `queryselector`

**For Groups of Elements:**
- Use `class` method for elements sharing classes
- Use `tag` method for element types
- Use `name` method for form elements

**For Text-Based Selection:**
- Use `text` for exact text matches
- Use `partialtext` for content containing specific text

**For Attribute-Based Selection:**
- Use `attribute` method for data attributes or custom attributes
- Use CSS attribute selectors with `queryselector`

### Step 3: Consider Edge Cases

**Dynamic Content:**
- Elements that may be added/removed dynamically
- Content that changes based on state

**Similar Elements:**
- Multiple elements with similar classes
- Nested structures with repeated patterns

**Visibility States:**
- Hidden elements (display: none, visibility: hidden)
- Conditionally visible content

## Query Construction Examples

### Common Element Selection Patterns

#### Button Selection
```json
// By ID
{"query": "submit-button", "method": "id"}

// By CSS class
{"query": ".btn-primary", "method": "queryselector"}

// By text content
{"query": "Submit", "method": "partialtext"}

// By data attribute
{"query": "[data-action='submit']", "method": "queryselector"}

// By type and class combination
{"query": "button.primary", "method": "queryselector"}
```

#### Form Element Selection
```json
// Input by name
{"query": "email", "method": "name"}

// Input by ID
{"query": "user-email", "method": "id"}

// All inputs of specific type
{"query": "input[type='text']", "method": "queryselector"}

// Form validation elements
{"query": "[data-validation='required']", "method": "queryselector"}
```

#### Content Selection
```json
// Headers by level
{"query": "h1", "method": "tag"}

// Specific header by text
{"query": "Welcome", "method": "partialtext"}

// Content within sections
{"query": ".content-section p", "method": "queryselector"}

// List items
{"query": "li", "method": "tag"}
```

#### Navigation Elements
```json
// Navigation links
{"query": "nav a", "method": "queryselector"}

// Active/focused elements
{"query": "[data-focused='true']", "method": "queryselector"}

// Menu items
{"query": ".menu-item", "method": "queryselector"}
```

#### Complex Selections
```json
// Nested element selection
{"query": ".card .title", "method": "queryselector"}

// Sibling selection
{"query": "label + input", "method": "queryselector"}

// Attribute contains
{"query": "[class*='btn']", "method": "queryselector"}

// Multiple class selection
{"query": ".active.highlighted", "method": "queryselector"}
```

### XPath Examples for Complex Queries
```json
// Text-based with context
{"query": "//div[@class='section']//p[contains(text(), 'specific text')]", "method": "xpath"}

// Parent-child relationships
{"query": "//button[parent::form[@id='main-form']]", "method": "xpath"}

// Following sibling
{"query": "//label[text()='Username']/following-sibling::input", "method": "xpath"}

// Attribute conditions
{"query": "//div[@data-type='content' and contains(@class, 'active')]", "method": "xpath"}
```

### TreeWalker Examples
```json
// Filter by tag name
{"query": "tagname=input", "method": "treewalker"}

// Filter by class name
{"query": "classname=active", "method": "treewalker"}

// Filter by ID
{"query": "id=main-content", "method": "treewalker"}
```

---

## Future Claude Instructions

### Step-by-Step Workflow for HTML Analysis

When a user provides an HTML file and asks for element queries, follow this process:

#### 1. HTML Analysis Phase
- Read and analyze the HTML structure
- Identify key elements: IDs, classes, tags, data attributes
- Note the content hierarchy and relationships
- Identify unique selectors for target elements

#### 2. Query Method Selection
Choose the most appropriate query method based on:

**Use `id` when:**
- Element has a unique ID attribute
- Most efficient for single element selection

**Use `class` or `classname` when:**
- Elements share common class names
- Need to find multiple similar elements

**Use `queryselector` when:**
- Need complex CSS selectors
- Combining multiple attributes/classes
- Using pseudo-selectors or combinators

**Use `xpath` when:**
- Need complex text-based selections
- Requiring parent/sibling relationships
- Advanced filtering capabilities

**Use `text` or `partialtext` when:**
- Selecting by visible text content
- Text is the most reliable identifier

**Use `attribute` when:**
- Selecting by data attributes or custom attributes
- Attribute values are unique identifiers

**Use `tag` when:**
- Need all elements of a specific type
- Counting element types

#### 3. Extraction Function Selection

**For existence checks:** Use `exists`
**For counting:** Use `count`
**For text content:** Use `textContent` or `innerText`
**For attributes:** Use `attribute` with attribute parameter
**For form data:** Use `value`, `checked`, `selected`
**For styling:** Use `computedStyle`, `style`, `isVisible`
**For positioning:** Use `getBoundingClientRect`, offset/scroll functions

#### 4. Query Construction

Always provide queries in proper JSON format:
```json
{
    "query": "selector or search term",
    "method": "method name",
    "attribute": "attribute name if needed"
}
```

#### 5. Validation Guidelines

**Ensure queries are:**
- Specific enough to avoid false positives
- Robust enough to handle minor HTML changes
- Using the most efficient method available
- Properly escaped for JSON format

#### 6. Response Format

Provide both the query configuration and expected extraction function:

```
Query: {"query": "element-selector", "method": "method"}
Extraction Function: "function_name"
```

### Method Selection Decision Tree

```
Is the element unique with an ID?
├─ YES → Use "id" method
└─ NO → Does it have distinctive text content?
    ├─ YES → Use "text" or "partialtext" method
    └─ NO → Does it have unique class combinations?
        ├─ YES → Use "queryselector" with class selectors
        └─ NO → Does it have data attributes?
            ├─ YES → Use "attribute" method
            └─ NO → Use "queryselector" with complex selectors
```

### Common Selection Strategies

#### For Interactive Elements (buttons, links, inputs)
1. Check for unique IDs first
2. Look for data attributes (data-action, data-testid, etc.)
3. Use text content if unique
4. Combine class selectors
5. Use form name attributes for inputs

#### For Content Elements (headings, paragraphs, lists)
1. Use text-based selection for unique content
2. Use tag methods for element types
3. Use CSS selectors for styled content
4. Consider hierarchical selectors (parent > child)

#### For Dynamic/State Elements
1. Look for state-based attributes (data-focused, aria-selected)
2. Use visibility checks with isVisible extraction
3. Consider multiple fallback selectors

### Error Handling and Troubleshooting

#### Common Issues and Solutions

**Query returns no results:**
- Verify element exists in HTML
- Check selector syntax
- Try broader selectors first, then narrow down
- Use `partialtext` instead of `text`

**Multiple elements when expecting one:**
- Add more specific selectors
- Use ID method when possible
- Add context with parent selectors

**Attribute extraction fails:**
- Ensure attribute parameter is provided
- Verify attribute exists on elements
- Check attribute name spelling

**Style queries return unexpected results:**
- Use `computedStyle` for actual rendered values
- Use `style` only for inline styles
- Consider inheritance and specificity

#### Testing Approach

1. Start with broad queries to verify elements exist
2. Narrow down with more specific selectors
3. Test extraction functions separately
4. Validate results match expectations
5. Consider edge cases and error conditions

### Final Recommendations

- Always prioritize readability and maintainability
- Use the most specific selector that reliably identifies elements
- Consider performance implications of different query methods
- Provide fallback approaches when possible
- Document complex queries with explanations
- Test queries against multiple screen variations when available

This documentation serves as a comprehensive reference for analyzing HTML structures and constructing ViewQueryEngine queries. Use it to provide accurate, efficient, and reliable element selection solutions.