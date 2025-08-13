# Benchmark Test Files

This directory contains benchmark test files used for performance testing and benchmarking of the gqlex library.

## File Categories

### Query Complexity Tests
- **complex_nested_query_*** - Complex nested GraphQL queries for testing deep nesting performance
- **complex_query_*** - Complex GraphQL queries for performance testing
- **fragment_query_*** - GraphQL queries with fragments for testing fragment processing performance

### Document Size Tests
- **large_document_*** - Large GraphQL documents for testing memory usage and processing time
- **simple_query_*** - Simple GraphQL queries for baseline performance comparison

### Performance Tests
- **cache_cold_*** - Tests for cold cache performance (first-time loading)
- **memory_test_*** - Memory usage and stress tests
- **mutation_*** - GraphQL mutation tests for performance benchmarking
- **multiple_xpath_*** - Multiple XPath processing tests for concurrent performance
- **builtin_test_*** - Built-in performance comparison tests

## Usage

These files are used by benchmark test classes that are commented out by default:
- `SimplePerformanceTest`
- `PerformanceComparisonTest`
- Performance methods in `LazyXPathProcessorTest`

## Running Benchmarks

To run benchmark tests:
1. Uncomment the benchmark test classes
2. Run with: `mvn test -Dtest="*Performance*"`
3. Re-comment after benchmarking is complete

## File Naming Convention

Files follow the pattern: `{test_type}_{timestamp}` where:
- `test_type` describes the test category
- `timestamp` is a unique identifier for the test run

## Maintenance

- These files are generated during performance testing
- Old benchmark files can be cleaned up periodically
- Keep recent benchmark files for performance trend analysis
