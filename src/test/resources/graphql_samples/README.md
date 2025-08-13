# GraphQL Samples Directory

This directory contains organized GraphQL samples used for testing and performance analysis of the gqlex library.

## 📁 Directory Structure

```
graphql_samples/
├── performance_tests/          # Performance test files (.graphql)
│   ├── README.md              # Performance test documentation
│   ├── simple_query_*.graphql # Basic query performance tests
│   ├── complex_nested_*.graphql # Complex nested query tests
│   ├── large_document_*.graphql # Large document performance tests
│   ├── fragment_query_*.graphql # Fragment processing tests
│   ├── mutation_*.graphql     # Mutation performance tests
│   ├── multiple_xpath_*.graphql # Multiple xpath processing tests
│   ├── memory_test_*.graphql  # Memory usage analysis tests
│   └── cache_cold_*.graphql   # Cache performance tests
├── benchmark/                  # Benchmark test files (generated during testing)
│   ├── README.md              # Benchmark documentation
│   ├── complex_nested_*       # Complex nested query benchmarks
│   ├── cache_cold_*           # Cache performance benchmarks
│   ├── large_document_*       # Large document benchmarks
│   ├── memory_test_*          # Memory usage benchmarks
│   ├── mutation_*             # Mutation benchmarks
│   ├── simple_query_*         # Simple query benchmarks
│   ├── multiple_xpath_*       # Multiple xpath benchmarks
│   └── fragment_query_*       # Fragment query benchmarks
└── original_tests/            # Original test files (.txt)
    ├── payload/               # Payload test files
    │   └── kiril_payload.txt  # Kiril payload test
    ├── simple_*.txt          # Simple query tests
    ├── multi_*.txt           # Multi-selection tests
    ├── hero_*.txt            # Hero example tests
    ├── intuit_*.txt          # Intuit real-world tests
    ├── fragment_*.txt        # Fragment tests
    └── mutation_*.txt        # Mutation tests
```

## 🚀 Performance Tests

The `performance_tests/` directory contains GraphQL files specifically created for performance analysis of the lazy loading gqlXPath system.

### File Categories

| Category | Description | Size Range | Purpose |
|----------|-------------|------------|---------|
| **Simple Queries** | Basic field selection queries | ~68 bytes | Baseline performance |
| **Complex Nested** | Deeply nested queries | ~675 bytes | Nested structure performance |
| **Large Documents** | Massive GraphQL documents | ~26KB | Large document handling |
| **Fragment Queries** | Queries with fragments | ~476 bytes | Fragment processing |
| **Mutations** | GraphQL mutations | ~451 bytes | Mutation processing |
| **Multiple XPath** | Multi-xpath processing | ~196 bytes | Batch processing |
| **Memory Tests** | Memory usage analysis | ~208 bytes | Memory optimization |
| **Cache Tests** | Cache performance | ~150 bytes | Caching analysis |

### Performance Metrics

These files are used to measure:
- **Processing Speed**: 2-6x improvement with lazy loading
- **Memory Usage**: 60-95% reduction in memory consumption
- **Scalability**: Performance with large documents
- **Cache Efficiency**: Hit/miss ratios and performance

## 📋 Original Tests

The `original_tests/` directory contains the original test files used for functional testing of the gqlex library.

### Test Categories

| Category | Description | Files |
|----------|-------------|-------|
| **Simple Tests** | Basic query functionality | `simple_*.txt` |
| **Multi-Selection** | Multiple field selection | `multi_*.txt` |
| **Hero Examples** | GraphQL hero examples | `hero_*.txt` |
| **Intuit Tests** | Real-world Intuit queries | `intuit_*.txt` |
| **Fragment Tests** | Fragment operations | `fragment_*.txt` |
| **Mutation Tests** | GraphQL mutations | `mutation_*.txt` |
| **Payload Tests** | Response payload tests | `payload/` |

## 🧪 Usage in Tests

### Performance Tests
```java
// Load performance test file
String documentId = "large_document_1754247871444.graphql";
LazyXPathProcessor processor = new LazyXPathProcessor();
LazyXPathResult result = processor.processXPath(documentId, "//user");
```

### Original Tests
```java
// Load original test file
String query = Files.readString(Paths.get("src/test/resources/graphql_samples/original_tests/simple_query.txt"));
GraphQLTransformer transformer = new GraphQLTransformer();
TransformationResult result = transformer.transform(query);
```

## 📊 File Naming Convention

### Performance Tests
- Format: `{category}_{timestamp}.graphql`
- Example: `simple_query_1754247871452.graphql`
- Timestamp ensures unique file names for parallel testing

### Original Tests
- Format: `{description}.txt`
- Example: `simple_query.txt`, `multi_select_hero_example_directives_variables.txt`
- Descriptive names for easy identification

## 🔧 Maintenance

### Adding New Performance Tests
1. Create new `.graphql` file in `performance_tests/`
2. Use timestamp suffix for uniqueness
3. Update this README with new category
4. Add to performance test suite

### Adding New Original Tests
1. Create new `.txt` file in `original_tests/`
2. Use descriptive naming convention
3. Add to appropriate test category
4. Update test documentation

## 📈 Performance Analysis

The performance test files are used by:
- `PerformanceComparisonTest.java` - Lazy vs traditional comparison
- `LazyXPathProcessorTest.java` - Lazy loading functionality tests
- `QuickPerformanceDemo.java` - Performance demonstration
- Custom performance analysis scripts

## 🎯 Best Practices

1. **Keep files organized** by category and purpose
2. **Use descriptive names** for easy identification
3. **Maintain documentation** for each test category
4. **Regular cleanup** of old performance test files
5. **Version control** all test files for reproducibility

## 📝 Notes

- Performance test files are generated during test execution
- Original test files are manually created and maintained
- All files are used in automated test suites
- Performance metrics are tracked and documented
- File organization supports scalable testing 