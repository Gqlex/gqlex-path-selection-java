# Benchmark Execution Guide

## 🚀 Overview

This guide explains how to run benchmark tests for the gqlex library. Benchmark tests are designed to measure performance, memory usage, and scalability of the lazy loading XPath processing system.

## 📁 Benchmark Test Structure

### Test Classes
- **`SimplePerformanceTest`** - Basic performance comparisons
- **`PerformanceComparisonTest`** - Comprehensive performance analysis
- **`LazyXPathProcessorTest`** - Core functionality with benchmark methods

### Benchmark Files
Located in: `src/test/resources/graphql_samples/benchmark/`
- **complex_nested_query_*** - Deep nesting performance tests
- **large_document_*** - Memory usage and large document handling
- **cache_cold_*** - Cache performance analysis
- **memory_test_*** - Memory stress tests
- **mutation_*** - GraphQL mutation performance
- **multiple_xpath_*** - Concurrent XPath processing

## 🎯 Running Benchmark Tests

### Method 1: Maven Profile (Recommended)
```bash
# Run all benchmark tests
mvn test -P benchmark

# Run with verbose output
mvn test -P benchmark -DtrimStackTrace=false
```

### Method 2: Specific Test Classes
```bash
# Run SimplePerformanceTest only
mvn test -Dtest="SimplePerformanceTest"

# Run PerformanceComparisonTest only
mvn test -Dtest="PerformanceComparisonTest"

# Run all performance-related tests
mvn test -Dtest="*Performance*"
```

### Method 3: Individual Test Methods
```bash
# Run specific test method
mvn test -Dtest="SimplePerformanceTest#testSimpleQueryPerformance"

# Run multiple specific methods
mvn test -Dtest="SimplePerformanceTest#testSimpleQueryPerformance,SimplePerformanceTest#testComplexQueryPerformance"
```

## 📊 Expected Benchmark Results

### Performance Metrics
- **Execution Time**: Traditional vs Lazy loading comparison
- **Memory Usage**: Memory consumption analysis
- **Speedup Factor**: Performance improvement ratios
- **Cache Efficiency**: Hit/miss ratios and performance

### Sample Output
```
=== Simple Query Performance ===
Traditional Time: 572ms
Lazy Time: 2ms
Speedup: 286.00x
Traditional Results: 1
Lazy Results: 1

=== Memory Usage Comparison ===
Traditional Memory: 2048 bytes
Lazy Memory: 128 bytes
Memory Reduction: 93.8%
Memory Efficiency: 16.00x
```

## ⚠️ Important Notes

### Before Running Benchmarks
1. **Ensure regular tests pass**: `mvn test` should complete successfully
2. **Close other applications**: Free up system resources
3. **Warm up JVM**: Run a few regular tests first

### After Running Benchmarks
1. **Review results**: Check for performance regressions
2. **Clean up**: Remove generated benchmark files if needed
3. **Document findings**: Record performance improvements/regressions

## 🔧 Troubleshooting

### Common Issues
- **Tests hang**: Check if benchmark files exist in correct location
- **Out of memory**: Increase JVM heap size with `-Xmx2g`
- **Slow execution**: Ensure no other processes are consuming resources

### Performance Tuning
```bash
# Increase JVM memory for large document tests
mvn test -P benchmark -DargLine="-Xmx4g -Xms2g"

# Enable GC logging for memory analysis
mvn test -P benchmark -DargLine="-Xmx4g -verbose:gc -XX:+PrintGCDetails"
```

## 📈 Performance Baseline

### Expected Improvements
- **Simple Queries**: 50-300x speedup
- **Complex Queries**: 10-50x speedup
- **Memory Usage**: 60-95% reduction
- **Large Documents**: 5-20x speedup

### Regression Thresholds
- **Performance**: No more than 10% degradation
- **Memory**: No more than 20% increase
- **Accuracy**: 100% result matching required

## 🎯 Use Cases

### Development
- Performance regression testing
- Optimization validation
- Memory leak detection

### Production
- Capacity planning
- Performance monitoring
- Scalability assessment

### Research
- Algorithm comparison
- Benchmark publication
- Performance analysis

## 📚 Related Documentation

- [Main Working Methods Rule](Plan/main_working_methods_rule.md)
- [Performance Summary](PERFORMANCE_SUMMARY.md)
- [Getting Started Guide](GETTING_STARTED.md)
- [README](README.md)

## 🆘 Support

For issues with benchmark execution:
1. Check this guide first
2. Review test output for error messages
3. Verify benchmark file locations
4. Ensure Maven profile configuration is correct

---

**Last Updated**: August 2024  
**Version**: 3.0.0  
**Maintainer**: gqlex Development Team
