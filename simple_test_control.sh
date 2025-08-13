#!/bin/bash

# Simple Test Control Script for gqlex Library

case "$1" in
    "fast"|"default")
        echo "Enabling FAST mode (all tests except performance/benchmark)..."
        
        # Re-enable all tests first
        find src/test/java -name "*Test.java.disabled" | while read -r testfile; do
            mv "$testfile" "${testfile%.disabled}"
        done
        
        # Disable only performance/benchmark tests
        find src/test/java -name "*Test.java" | grep -E "(Performance|Benchmark|Optimization)" | while read -r testfile; do
            echo "   ❌ Disabling $(basename "$testfile")"
            mv "$testfile" "$testfile.disabled"
        done
        
        echo "✅ Fast mode enabled. All tests will run EXCEPT performance/benchmark tests."
        echo "   Run: mvn test -q"
        ;;
        
    "all")
        echo "Enabling ALL tests (including performance/benchmark)..."
        # Re-enable all disabled tests
        find src/test/java -name "*Test.java.disabled" | while read -r testfile; do
            mv "$testfile" "${testfile%.disabled}"
        done
        
        echo "✅ All tests enabled. This may take a long time to run."
        echo "   Run: mvn test -q"
        ;;
        
    "benchmark")
        echo "Enabling BENCHMARK tests only..."
        # First enable all tests
        find src/test/java -name "*Test.java.disabled" | while read -r testfile; do
            mv "$testfile" "${testfile%.disabled}"
        done
        
        # Disable non-benchmark tests
        find src/test/java -name "*Test.java" | grep -v -E "(Performance|Benchmark|Optimization)" | while read -r testfile; do
            echo "   ❌ Disabling $(basename "$testfile")"
            mv "$testfile" "$testfile.disabled"
        done
        
        echo "✅ Benchmark tests enabled. Only performance/benchmark tests will run."
        echo "   Run: mvn test -q"
        ;;
        
    "status")
        echo "Current test status:"
        echo "===================="
        
        echo "📁 Performance/Benchmark tests:"
        find src/test/java -name "*Test.java" | grep -E "(Performance|Benchmark|Optimization)" | while read -r testfile; do
            if [ -f "$testfile.disabled" ]; then
                echo "   ❌ $(basename "$testfile") (DISABLED)"
            else
                echo "   ✅ $(basename "$testfile") (ENABLED)"
            fi
        done
        
        echo ""
        echo "📁 Regular tests:"
        find src/test/java -name "*Test.java" | grep -v -E "(Performance|Benchmark|Optimization)" | while read -r testfile; do
            if [ -f "$testfile.disabled" ]; then
                echo "   ❌ $(basename "$testfile") (DISABLED)"
            else
                echo "   ✅ $(basename "$testfile") (ENABLED)"
            fi
        done
        
        echo ""
        echo "🔧 Usage:"
        echo "   ./simple_test_control.sh fast      - Run all tests EXCEPT performance/benchmark (default)"
        echo "   ./simple_test_control.sh all       - Run ALL tests (including performance/benchmark)"
        echo "   ./simple_test_control.sh benchmark - Run ONLY performance/benchmark tests"
        echo "   ./simple_test_control.sh status    - Show current test configuration"
        ;;
        
    *)
        echo "Simple Test Control Script for gqlex Library"
        echo "==========================================="
        echo ""
        echo "Usage: $0 {fast|default|all|benchmark|status}"
        echo ""
        echo "Modes:"
        echo "  fast/default - Run all tests EXCEPT performance/benchmark (recommended for development)"
        echo "  all          - Run ALL tests including performance/benchmark (may take hours)"
        echo "  benchmark    - Run ONLY performance/benchmark tests"
        echo "  status       - Show current test configuration"
        echo ""
        echo "Examples:"
        echo "  ./simple_test_control.sh fast      # Enable fast mode (all tests except performance)"
        echo "  mvn test -q                        # Run tests"
        echo "  ./simple_test_control.sh all       # Enable all tests (including performance)"
        echo "  ./simple_test_control.sh benchmark # Enable only performance tests"
        ;;
esac
