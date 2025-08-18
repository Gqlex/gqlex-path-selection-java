# Main Working Methods & Rules - gqlex Library Development

## 🎯 Core Development Principles

### **Code Quality & Testing**
- ✅ **All tests must 100% successfully passed** - No exceptions, no compromises
- ✅ **Test verification before any commit** - Run `mvn test -Pfast` to ensure 100% success
- ✅ **Maintain existing functionality** - Never break working features
- ✅ **Generic solutions only** - Code must be schema-agnostic and reusable

### **Git Workflow & Version Control**
- 🚫 **No commits without explicit user approval** - Always ask before committing
- 🚫 **No pushes without explicit user approval** - Wait for user confirmation
- 🚫 **No merges without explicit user approval** - Main branch protection is critical
- ✅ **Verify tests before any git operation** - Ensure code quality before version control
- 🚫 **if branch ahs not been pushed to master or main, i must know this, do not continue, untill the brnach will be merged to masters or main**

## 🔧 Development Workflow

### **Before Making Changes**
1. **Understand the requirement** - Clarify what needs to be implemented
2. **Check existing features** - Review `supported_features.md` to avoid duplication
3. **Plan the approach** - Design generic, reusable solutions
4. **Consider test impact** - Ensure changes won't break existing tests

### **During Development**
1. **Write generic code** - Avoid hardcoded assumptions about GraphQL schemas
2. **Follow existing patterns** - Maintain consistency with current architecture
3. **Add comprehensive tests** - Cover new functionality thoroughly
4. **Update documentation** - Keep `supported_features.md` current

### **Before Committing**
1. **Run full test suite** - `mvn test -Pfast` must pass 100%
2. **Verify no regressions** - Ensure existing features still work
3. **Check code quality** - Follow Java best practices and project conventions
4. **Get user approval** - Explicit confirmation required for any git operations

## 📁 File Organization Rules

### **Test Resources**
- **Benchmark files**: `src/test/resources/gqlex_samples/benchmark/`
- **Test samples**: `src/test/resources/gqlex_samples/`
- **Test files**: `src/test/java/com/intuit/gqlex/`

### **Documentation Updates**
- **Feature documentation**: Update `supported_features.md` for new capabilities
- **Release notes**: Update `RELEASE_NOTES.md` for version changes
- **Memory state**: Update `Plan/memory_state.md` for development tracking, you must track all works to be dokne and works that has been in the memory state
- **Getting started**: Update `GETTING_STARTED.md` for user guidance

## 🧪 Testing Strategy

### **Test Profiles**
- **Fast profile**: `mvn test -Pfast` - Regular tests, excludes benchmarks
- **Benchmark profile**: `mvn test -Pbenchmark` - Performance tests only
- **All tests**: `mvn test` - Complete test suite

### **Test Requirements**
- **Unit tests**: Individual component testing
- **Integration tests**: Component interaction testing
- **Performance tests**: Benchmark validation
- **Coverage targets**: Maintain high code coverage

## 🚫 Strict Prohibitions

### **Code Deletion**
- 🚫 **Never delete existing features** - All features in `supported_features.md` are protected
- 🚫 **Never remove working classes** - Maintain backward compatibility
- 🚫 **Never delete test resources** - Keep all test data and samples
- 🚫 **Never remove documentation** - Preserve knowledge and guides

### **Git Operations**
- 🚫 **No unauthorized commits** - User approval required
- 🚫 **No unauthorized pushes** - User approval required
- 🚫 **No unauthorized merges** - User approval required
- 🚫 **No force operations** - Protect repository integrity

## ✅ Required Actions

### **After Each Development Iteration**
1. **Update `memory_state.md`** - Document progress and changes
2. **Verify test coverage** - Ensure new code is tested
3. **Update documentation** - Keep `supported_features.md` current
4. **Prepare for review** - Ready for user approval

### **Before User Review**
1. **Self-review changes** - Ensure quality and completeness
2. **Test verification** - Confirm 100% test success
3. **Documentation sync** - All docs reflect current state
4. **Clear summary** - Explain what was implemented

## 🎯 Success Criteria

### **Development Success**
- ✅ **100% successfully test pass rate** - All tests successful
- ✅ **No regressions** - Existing features work perfectly
- ✅ **Generic solutions** - Code is reusable and flexible
- ✅ **Complete documentation** - All features documented

### **User Satisfaction**
- ✅ **Clear communication** - Requirements understood and met
- ✅ **Quality delivery** - Production-ready code
- ✅ **Proper workflow** - Following established processes
- ✅ **Documentation accuracy** - Reflects actual capabilities

---

**Last Updated**: December 2024  
**Version**: 3.1.0  
**Status**: Active Development Rules  
**Purpose**: Ensure consistent, high-quality development workflow
