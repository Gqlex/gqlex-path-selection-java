# Test Control System for gqlex Library

## Overview

This system ensures that by default, **ONLY FAST TESTS** run, preventing the long-running benchmark and heavy tests from executing during regular development.

## Quick Start

### Default Mode (Fast Tests Only)
```bash
# Tests run in < 1 second by default
mvn test -q
```

### Control Test Execution
```bash
# Show current status
./test_control.sh status

# Enable only fast tests (default mode)
./test_control.sh fast

# Enable all tests (may take hours)
./test_control.sh all

# Enable only benchmark/performance tests
./test_control.sh benchmark
```

## Test Modes

### 🚀 Fast Mode (Default)
- **Command**: `./test_control.sh fast`
- **Tests**: Only `LazyXPathProcessorTest` (3 sanity check tests)
- **Duration**: < 1 second
- **Use Case**: Daily development, CI/CD, quick verification

### 🔥 All Tests Mode
- **Command**: `./test_control.sh all`
- **Tests**: All test classes including heavy ones
- **Duration**: May take hours
- **Use Case**: Full testing before releases, debugging

### 📊 Benchmark Mode
- **Command**: `./test_control.sh benchmark`
- **Tests**: Only performance and benchmark tests
- **Duration**: Varies (may be long)
- **Use Case**: Performance testing, optimization work

## How It Works

The system works by temporarily renaming test files:
- **Fast mode**: All test files except `LazyXPathProcessorTest.java` are renamed to `*.java.disabled`
- **All mode**: All test files are restored to their original names
- **Benchmark mode**: Only performance-related test files are enabled

## File Structure

```
src/test/java/
├── com/intuit/gqlex/
│   ├── gqlxpath/lazy/
│   │   ├── LazyXPathProcessorTest.java          # ✅ Fast tests (always enabled)
│   │   ├── PerformanceComparisonTest.java       # ❌ Disabled by default
│   │   ├── SimplePerformanceTest.java           # ❌ Disabled by default
│   │   └── QuickPerformanceDemo.java            # ❌ Disabled by default
│   ├── linting/core/
│   │   ├── GraphQLLinterTest.java               # ❌ Disabled by default
│   │   └── LintResultTest.java                  # ❌ Disabled by default
│   └── ... (other test packages)
```

## Why This Approach?

1. **Reliability**: File renaming is more reliable than Maven configuration
2. **Simplicity**: Easy to understand and debug
3. **Performance**: Fast tests run in seconds, not hours
4. **Flexibility**: Easy to switch between modes as needed

## Troubleshooting

### Tests Still Running Slowly?
```bash
# Check current status
./test_control.sh status

# Ensure fast mode is enabled
./test_control.sh fast

# Verify only fast test runs
mvn test -q
```

### Need to Run Specific Tests?
```bash
# Enable all tests temporarily
./test_control.sh all

# Run specific test class
mvn test -Dtest="SpecificTestClass" -q

# Return to fast mode
./test_control.sh fast
```

### File Permission Issues?
```bash
# Make script executable
chmod +x test_control.sh
```

## Best Practices

1. **Always use fast mode for development** - `./test_control.sh fast`
2. **Use all mode only when needed** - `./test_control.sh all`
3. **Use benchmark mode for performance work** - `./test_control.sh benchmark`
4. **Check status before running tests** - `./test_control.sh status`

## Integration with CI/CD

For CI/CD pipelines, the system automatically runs in fast mode by default, ensuring:
- Quick feedback on code changes
- Reliable test execution
- Fast build times

## Support

If you encounter issues:
1. Check the current status: `./test_control.sh status`
2. Reset to fast mode: `./test_control.sh fast`
3. Verify fast tests run: `mvn test -q`

---

**Remember**: By default, only fast tests run. This prevents the long-running tests from hanging your development workflow!
