package com.intuit.gqlex.validation.rules;

import com.intuit.gqlex.validation.core.ValidationContext;
import com.intuit.gqlex.validation.core.ValidationResult;
import com.intuit.gqlex.validation.core.ValidationLevel;

/**
 * Base interface for all GraphQL validation rules.
 * This interface is completely generic and works with any GraphQL validation scenario.
 */
public abstract class ValidationRule {
    
    protected final String name;
    protected final ValidationLevel level;
    protected final String description;
    
    /**
     * Creates a validation rule with basic information.
     * 
     * @param name the name of the validation rule
     * @param level the severity level of the rule
     * @param description the description of what the rule validates
     */
    protected ValidationRule(String name, ValidationLevel level, String description) {
        this.name = name;
        this.level = level;
        this.description = description;
    }
    
    /**
     * Creates a validation rule with ERROR level.
     * 
     * @param name the name of the validation rule
     * @param description the description of what the rule validates
     */
    protected ValidationRule(String name, String description) {
        this(name, ValidationLevel.ERROR, description);
    }
    
    /**
     * Performs validation on the given context and adds results to the validation result.
     * This method is generic and works with any GraphQL document structure.
     * 
     * @param context the validation context
     * @param result the validation result to add issues to
     */
    public abstract void validate(ValidationContext context, ValidationResult result);
    
    /**
     * Gets the name of the validation rule.
     * 
     * @return the rule name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the severity level of the validation rule.
     * 
     * @return the validation level
     */
    public ValidationLevel getLevel() {
        return level;
    }
    
    /**
     * Gets the description of the validation rule.
     * 
     * @return the rule description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if the validation rule is enabled.
     * Subclasses can override this to provide conditional validation.
     * 
     * @param context the validation context
     * @return true if the rule should be executed
     */
    public boolean isEnabled(ValidationContext context) {
        return true;
    }
    
    /**
     * Gets the category of the validation rule.
     * This helps organize rules by type (e.g., "STRUCTURAL", "SECURITY", "PERFORMANCE").
     * 
     * @return the rule category
     */
    public String getCategory() {
        return "GENERAL";
    }
    
    /**
     * Returns a string representation of the validation rule.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("ValidationRule{name='%s', level=%s, category='%s', description='%s'}", 
            name, level, getCategory(), description);
    }
} 