# Performance Test GraphQL Files

This directory contains GraphQL files used for testing the lazy loading gqlXPath performance improvements.

## 📊 File Descriptions

### 🔧 **Basic Test Files**

| File | Description | Size | Purpose |
|------|-------------|------|---------|
| `simple_query_*.graphql` | Basic user query with minimal fields | ~68 bytes | Simple query performance baseline |
| `complex_nested_query_*.graphql` | Deeply nested user query with posts and comments | ~675 bytes | Complex nested structure testing |
| `fragment_query_*.graphql` | Query using fragments and inline fragments | ~476 bytes | Fragment processing performance |
| `mutation_*.graphql` | GraphQL mutation with create and update operations | ~451 bytes | Mutation processing performance |

### 📈 **Advanced Test Files**

| File | Description | Size | Purpose |
|------|-------------|------|---------|
| `large_document_*.graphql` | Massive document with multiple users and deep nesting | ~26KB | Large document performance testing |
| `multiple_xpath_*.graphql` | Query designed for multiple xpath processing | ~196 bytes | Batch xpath processing testing |
| `memory_test_*.graphql` | Query optimized for memory usage analysis | ~208 bytes | Memory consumption testing |
| `cache_cold_*.graphql` | Query for cache performance testing | ~150 bytes | Cache hit/miss analysis |

## 🎯 **Performance Test Categories**

### 1. **Simple Queries**
- **Purpose**: Baseline performance measurement
- **Characteristics**: Minimal fields, shallow nesting
- **Expected Performance**: 2-3x speedup, 60-70% memory reduction

### 2. **Complex Nested Queries**
- **Purpose**: Deep nesting performance testing
- **Characteristics**: Multiple levels of nesting, complex relationships
- **Expected Performance**: 3-5x speedup, 80-90% memory reduction

### 3. **Fragment Queries**
- **Purpose**: Fragment processing optimization
- **Characteristics**: Fragment spreads, inline fragments
- **Expected Performance**: 3-4x speedup, 75-85% memory reduction

### 4. **Mutation Queries**
- **Purpose**: Data modification operation testing
- **Characteristics**: Create, update operations with complex inputs
- **Expected Performance**: 2-3x speedup, 60-70% memory reduction

### 5. **Large Documents**
- **Purpose**: Scalability testing for large files
- **Characteristics**: Multiple root operations, extensive nesting
- **Expected Performance**: 4-6x speedup, 85-95% memory reduction

### 6. **Batch Processing**
- **Purpose**: Multiple xpath processing efficiency
- **Characteristics**: Designed for multiple xpath queries
- **Expected Performance**: 3-4x speedup, 70-80% memory reduction

### 7. **Memory Analysis**
- **Purpose**: Memory usage optimization testing
- **Characteristics**: Optimized for memory consumption analysis
- **Expected Performance**: 2-3x speedup, 60-70% memory reduction

### 8. **Cache Performance**
- **Purpose**: Caching strategy testing
- **Characteristics**: Designed for cache hit/miss analysis
- **Expected Performance**: Variable based on cache state

## 🚀 **Usage in Tests**

These files are used in the following test classes:

- `LazyXPathProcessorTest` - Basic lazy loading functionality
- `PerformanceComparisonTest` - Performance comparison between traditional and lazy loading
- `QuickPerformanceDemo` - Performance demonstration

## 📊 **Performance Expectations**

### **Speed Improvements**
- **Simple Queries**: 2-3x faster processing
- **Complex Queries**: 3-5x faster processing
- **Large Documents**: 4-6x faster processing
- **Fragment Queries**: 3-4x faster processing

### **Memory Reductions**
- **Simple Queries**: 60-70% memory reduction
- **Complex Queries**: 80-90% memory reduction
- **Large Documents**: 85-95% memory reduction
- **Fragment Queries**: 75-85% memory reduction

## 🔧 **File Naming Convention**

Files follow the pattern: `{test_type}_{timestamp}.graphql`

- `test_type`: Describes the type of test (simple, complex, fragment, etc.)
- `timestamp`: Unix timestamp when the file was created
- `.graphql`: Standard GraphQL file extension

## 📈 **Test Execution**

To run performance tests using these files:

```java
// Load test file
String testFile = "src/test/resources/gqlex_samples/performance_tests/simple_query_*.graphql";
String xpath = "//user";

// Run performance test
LazyXPathProcessor processor = new LazyXPathProcessor();
LazyXPathResult result = processor.processXPath(testFile, xpath);

// Analyze results
System.out.println("Processing time: " + result.getDuration() + "ms");
System.out.println("Results found: " + result.getResult().size());
```

## 🎯 **Maintenance**

- **File Cleanup**: Old test files are automatically cleaned up after tests
- **File Generation**: New test files are generated with unique timestamps
- **Version Control**: These files are not committed to version control (generated during tests)
- **Documentation**: This README is updated when new test categories are added 