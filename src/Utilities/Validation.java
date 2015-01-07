package Utilities;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.java.balloontip.BalloonTip;

/**
 * Common validation method class. This class will handle validation of most JComponent classes or primitive types.
 * If the JComponent value is found to be not valid then a balloon message will be displayed and the validation will return false.
 * @usage
 * Typical Usage:
        Validation validation = new Validation();
        validation.addObjectToValidate(jTextFieldMinScoreDiff, Validation.NOT_EMPTY);
        validation.addObjectToValidate(jTextFieldMinScoreDiff, Validation.DOUBLE);
        Map optionsMinScoreDiff = new HashMap();
        optionsMinScoreDiff.put(Validation.OptionTypes.GREATER_THAN_VALUE, -0.1d);
        optionsMinScoreDiff.put(Validation.OptionTypes.ERROR_MESSAGE, "Min score differential needs to be greater than 0.");
        validation.addObjectToValidate(jTextFieldMinScoreDiff, Validation.COMPARE_VALUE, optionsMinScoreDiff);
 *
 * @author Adam
 * http://balloontip.java.net/manual.html
 */
public class Validation {

    /**
     * Holds a list of object maps to validate.
     */
    private ArrayList<Map> objectsToValidate = new ArrayList<Map>();
    /**
     * Holds a list of object maps that have validation errors.
     */
    private ArrayList<Map> validationErrors = new ArrayList<Map>();
    /**
     * Holds a list of all the balloon tips to be displayed for the fields with validation errors.
     */
    private ArrayList<BalloonTip> validationErrorTips = new ArrayList<BalloonTip>();
    /**
     * Do integer validation for object.
     */
    public static final int INTEGER = 0;
    /**
     * Do double validation for object.
     */
    public static final int DOUBLE = 1;
    /**
     * Do file name validation for object.
     */
    public static final int FILE_NAME = 2;
    /**
     * Check if an object is not empty.
     */
    public static final int NOT_EMPTY = 3;
    /**
     * Do comparison validation for object.
     */
    public static final int COMPARE_VALUE = 5;
    /**
     * Do custom method validation for object.
     */
    public static final int CUSTOM_METHOD = 6;
    /**
     * Do string pattern matching validation for object.
     */
    public static final int PATTERN_MATCH = 7;
    /**
     * Do percent validation for object.
     */
    public static final int PERCENT = 8;
    /**
     * These are default error messages for the types listed above.
     */
    private static final String[] ERROR_MESSAGES = {
        "Integers values only",
        "Decimal values only",
        "Not a valid filename",
        "Value required, cannot be empty",
        "Template already exists",
        "Comparison error",
        "Invalid entry",
        "Invalid characters found",
        "Invalid percentage. Enter a decimal value between 0.0 and 100.0."
    };

    /**
     * These are the keys of the object map
     */
    private static enum ObjectMapKeys {

        /**
         * Object being validated
         */
        OBJECT,
        /**
         * Type of validation
         */
        TYPE,
        /**
         * Options for the validation
         */
        OPTIONS
    };

    /**
     * Available validation options
     */
    public static enum OptionTypes {

        /**
         * Set the error message. If not set a default message will be displayed.
         */
        ERROR_MESSAGE,
        /**
         * Set to true to close the error message on focus, default is true.
         */
        CLOSE_MESSAGE_ON_FOCUS,
        /**
         * Set to true to validate if the field is disabled. Default is false.
         */
        CHECK_IF_DISABLED,
        /**
         * When type is COMPARE_VALUE set this value to compare greater than the value of the field
         */
        GREATER_THAN_VALUE,
        /**
         * When type is COMPARE_VALUE set this value to compare less than the value of the field
         */
        LESS_THAN_VALUE,
        /**
         * When type is COMPARE_VALUE set this value to compare equal to the value of the field
         */
        EQUALS_VALUE,
        /**
         * When type is CUSTOM_METHOD set the custom method name to call. Required.
         */
        CUSTOM_METHOD_NAME,
        /**
         * When type is CUSTOM_METHOD set the parent of the custom method name to call. Required.
         */
        PARENT_CLASS,
        /**
         * When type is PATTERN_MATCH set the pattern to match. Required.
         */
        PATTERN_TO_MATCH,
        /**
         * Set to false to not validate the field on key up. Default is true.
         */
        VALIDATE_ON_KEY_UP,
        /**
         * Allow the value to be empty.
         */
        ALLOW_EMPTY
    };
    /**
     * Set to true to display errors after validate
     */
    private boolean displayErrorsAfterValidate = true;

    /**
     * Add object to be validated.
     * @param object to be validated
     * @param type of validation to preform
     */
    public void addObjectToValidate(Object object, int type) {
        Map tempMap = new HashMap();
        tempMap.put(ObjectMapKeys.OBJECT, object);
        tempMap.put(ObjectMapKeys.TYPE, new Integer(type));
        setToolTipWithValidationErrors(tempMap);
        if (doValidateOnKeyUpForObject(tempMap)) {
            ((JComponent) object).addKeyListener(new ValidateOnKeyUpListener());
        }
        objectsToValidate.add(tempMap);
    }

    /**
     * Add object to be validated.
     * @param object to be validated
     * @param type of validation to preform
     * @param options map of different options for the validation
     */
    public void addObjectToValidate(Object object, int type, Map options) {
        Map tempMap = new HashMap();
        tempMap.put(ObjectMapKeys.OBJECT, object);
        tempMap.put(ObjectMapKeys.TYPE, new Integer(type));
        tempMap.put(ObjectMapKeys.OPTIONS, options);
        setToolTipWithValidationErrors(tempMap);
        if (doValidateOnKeyUpForObject(tempMap)) {
            ((JComponent) object).addKeyListener(new ValidateOnKeyUpListener());
        }
        objectsToValidate.add(tempMap);
    }

    /**
     * Add object that didn't pass validation
     * @param object that didn't pass
     * @param type of validation
     * @param options map of different options for the validation
     */
    private void addObjectToValidationErrors(Object object, int type, Map options) {
        boolean addObject = true;
        Map tempMap = new HashMap();
        tempMap.put(ObjectMapKeys.OBJECT, object);
        tempMap.put(ObjectMapKeys.TYPE, new Integer(type));
        tempMap.put(ObjectMapKeys.OPTIONS, options);
        for (int i = 0; i < validationErrors.size(); i++) {
            if (getObjectFromObjectMap(validationErrors.get(i)) == object) {
                if (((Map) validationErrors.get(i).get(ObjectMapKeys.OPTIONS)) == null) {
                    validationErrors.get(i).put(ObjectMapKeys.OPTIONS, new HashMap());
                }
                ((Map) validationErrors.get(i).get(ObjectMapKeys.OPTIONS)).put(OptionTypes.ERROR_MESSAGE, getErrorMessageForObject(validationErrors.get(i)) + "<br/>" + getErrorMessageForObject(tempMap));
                addObject = false;
                break;
            }
        }
        if (addObject) {
            validationErrors.add(tempMap);
        }
    }

    /**
     * Loops through all the objects to validate and checks to see if they pass.
     * If there are any errors then they are displayed.
     * @return false if there are validation errors.
     */
    public boolean validate() {
        Object value = null;
        Map options = null;
        int type = -1;
        boolean enabled = false;
        boolean doValidateOnEmpty = true;

        disposeValidationErrorTips();
        validationErrors.clear();
        for (int i = 0; i < objectsToValidate.size(); i++) {
            value = getValueFromObject(objectsToValidate.get(i));
            type = getTypeFromObjectMap(objectsToValidate.get(i));
            options = getOptionsForObjectMap(objectsToValidate.get(i));
            enabled = isValidationEnabledForObject(objectsToValidate.get(i));
            doValidateOnEmpty = doValidationForEmptyValueFromObject(objectsToValidate.get(i));

            // If the validation does not pass.
            if (enabled && doValidateOnEmpty && !validateByType(value, type, options)) {
                addObjectToValidationErrors(objectsToValidate.get(i).get(ObjectMapKeys.OBJECT), type, options);
            }
        }
        if (hasValidationErrors() && isDisplayErrorsAfterValidate()) {
            displayValidationErrors();
        }
        return !hasValidationErrors();
    }

    /**
     * Will validate a single object and clear the rest of the validation errors. Allowing the
     * user to focus on one error at a time.
     * @param object to validation
     * @return false if there are validation errors.
     */
    private boolean validateObject(Object object) {
        Map objectMap = getObjectMapFromObject(object);
        disposeValidationErrorTips();
        validationErrors.clear();
        if (!objectMap.isEmpty()) {
            Object value = getValueFromObject(objectMap);
            int type = getTypeFromObjectMap(objectMap);
            Map options = getOptionsForObjectMap(objectMap);
            boolean enabled = isValidationEnabledForObject(objectMap);
            boolean doValidateOnEmpty = doValidationForEmptyValueFromObject(objectMap);

            // If the validation does not pass.
            if (enabled && doValidateOnEmpty && !validateByType(value, type, options)) {
                addObjectToValidationErrors(objectMap.get(ObjectMapKeys.OBJECT), type, options);
            }
        }
        if (hasValidationErrors() && isDisplayErrorsAfterValidate()) {
            displayValidationErrors();
        }
        return !hasValidationErrors();
    }

    /**
     * @return true if the number of validation errors is greater that 0.
     */
    public boolean hasValidationErrors() {
        return validationErrors.size() > 0;
    }

    /**
     * Displays balloon tips if the object is a JComponent or prints the message if it is not.
     */
    public void displayValidationErrors() {
        for (int i = 0; i < validationErrors.size(); i++) {
            if (isObjectFromObjectMapJComponent(validationErrors.get(i))) {
                BalloonTip errorTip = new BalloonTip((JComponent) getObjectFromObjectMap(validationErrors.get(i)), "<html>" + getErrorMessageForObject(validationErrors.get(i)) + "</html>");
                if (isCloseOnFocusForObject(validationErrors.get(i))) {
                    ((JComponent) getObjectFromObjectMap(validationErrors.get(i))).addFocusListener(new CloseToolTipFocusListener());
                }
                validationErrorTips.add(errorTip);
            } else {
                System.out.println(getObjectFromObjectMap(validationErrors.get(i)).toString() + ": " + getErrorMessageForObject(validationErrors.get(i)));
            }
        }
    }

    /**
     * Listens for a Component when it gains focus and has balloon tip, the balloon tip gets closed.
     */
    public class CloseToolTipFocusListener implements FocusListener {

        public void focusGained(FocusEvent e) {
            for (int i = 0; i < validationErrorTips.size(); i++) {
                if (validationErrorTips.get(i).getAttachedComponent() == e.getComponent()) {
                    validationErrorTips.get(i).closeBalloon();
                }
            }
        }

        public void focusLost(FocusEvent e) {
        }
    }

    /**
     * Will validate the objects on key up
     */
    public class ValidateOnKeyUpListener implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
            validateObject(e.getSource());
        }
    }

    /**
     * Closes all the error tips and clears the array list.
     */
    public void disposeValidationErrorTips() {
        for (int i = 0; i < validationErrorTips.size(); i++) {
            validationErrorTips.get(i).closeBalloon();
        }
        validationErrorTips.clear();
    }

    /**
     * Checks to see if the validating object is a JComponent
     * @param objectMap to extract the object from
     * @return true if a JComponent
     */
    private static boolean isObjectFromObjectMapJComponent(Map objectMap) {
        return getObjectFromObjectMap(objectMap) instanceof JComponent;
    }

    /**
     * Checks to see if the validating object has text input
     * @param objectMap to extract the object from
     * @return true if a JComponent
     */
    private static boolean isObjectFromObjectMapAcceptTextInput(Map objectMap) {
        if (getObjectFromObjectMap(objectMap) instanceof JTextField) {
            return true;
        }
        if (getObjectFromObjectMap(objectMap) instanceof JTextArea) {
            return true;
        }
        return false;
    }

    /**
     * Sets the tool tip for the validation object if it is a JComponent
     * @param objectMap to set the tool tip for.
     */
    private static void setToolTipWithValidationErrors(Map objectMap) {
        if (isObjectFromObjectMapJComponent(objectMap)) {
            ((JComponent) getObjectFromObjectMap(objectMap)).setToolTipText("<html>" + getErrorMessageForObject(objectMap) + "</html>");
        }
    }

    /**
     * Gets the object that is to be validated.
     * @param objectMap to extract the object from
     * @return the object
     */
    private static Object getObjectFromObjectMap(Map objectMap) {
        if (objectMap.containsKey(ObjectMapKeys.OBJECT)) {
            return (Object) objectMap.get(ObjectMapKeys.OBJECT);
        }
        return null;
    }

    /**
     * Get an object map from the objects to validate list given an object
     * @param object you are looking for
     * @return and empty map or the object map of the object.
     */
    private Map getObjectMapFromObject(Object object) {
        for (int i = 0; i < objectsToValidate.size(); i++) {
            if (objectsToValidate.get(i).get(ObjectMapKeys.OBJECT).equals(object)) {
                return objectsToValidate.get(i);
            }
        }
        return new HashMap();
    }

    /**
     * Finds the validation type given a specific object
     * @param objectMap to extract the value from
     * @return the type of validation
     */
    private static int getTypeFromObjectMap(Map objectMap) {
        if (objectMap.containsKey(ObjectMapKeys.TYPE)) {
            return (Integer) objectMap.get(ObjectMapKeys.TYPE);
        }
        return -1;
    }

    /**
     * Finds the validation options given a specific object
     * @param objectMap to extract the options from
     * @return the options for the object or null
     */
    private static Map getOptionsForObjectMap(Map objectMap) {
        if (objectMap.containsKey(ObjectMapKeys.OPTIONS)) {
            return (Map) objectMap.get(ObjectMapKeys.OPTIONS);
        }
        return null;
    }

    /**
     * Tries to find the error message for an object
     * @param objectMap to extract the message from
     * @return if no error message is found return the default
     */
    private static String getErrorMessageForObject(Map objectMap) {
        Map options = getOptionsForObjectMap(objectMap);
        if (options != null && options.containsKey(OptionTypes.ERROR_MESSAGE)) {
            return (String) options.get(OptionTypes.ERROR_MESSAGE);
        }
        return ERROR_MESSAGES[getTypeFromObjectMap(objectMap)];
    }

    /**
     * Checks to see if the validation for an object is enabled.
     * @param objectMap in question
     * @return true if the validation is enabled.
     */
    private static boolean isValidationEnabledForObject(Map objectMap) {
        Map options = getOptionsForObjectMap(objectMap);
        if (options != null && options.containsKey(OptionTypes.CHECK_IF_DISABLED) && (Boolean) options.get(OptionTypes.CHECK_IF_DISABLED)) {
            return true;
        }
        return isObjectEnabled(objectMap);
    }

    /**
     * If allow empty is set and the value is empty then return false for doing
     * validation on empty value.
     * @param objectMap in question
     * @return true if validation should be done on empty value
     */
    private static boolean doValidationForEmptyValueFromObject(Map objectMap) {
        Map options = getOptionsForObjectMap(objectMap);
        if (options != null && options.containsKey(OptionTypes.ALLOW_EMPTY) && (Boolean) options.get(OptionTypes.ALLOW_EMPTY)) {
            if (!isNotEmpty(getValueFromObject(objectMap))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check to see if the error balloon should be closed on field focus
     * @param objectMap in question
     * @return true if close on focus.
     */
    private static boolean isCloseOnFocusForObject(Map objectMap) {
        Map options = getOptionsForObjectMap(objectMap);
        if (options != null && options.containsKey(OptionTypes.CLOSE_MESSAGE_ON_FOCUS) && (Boolean) options.get(OptionTypes.CLOSE_MESSAGE_ON_FOCUS)) {
            return true;
        }
        return isObjectEnabled(objectMap);
    }

    /**
     * Check to see if the object should be validated on key up
     * @param objectMap in question
     * @return true if the object should be validated on key up.
     */
    private static boolean doValidateOnKeyUpForObject(Map objectMap) {
        Map options = getOptionsForObjectMap(objectMap);
        if (options != null
                && isObjectFromObjectMapAcceptTextInput(objectMap)
                && options.containsKey(OptionTypes.VALIDATE_ON_KEY_UP)
                && (Boolean) options.get(OptionTypes.VALIDATE_ON_KEY_UP)) {
            return true;
        }
        return isObjectEnabled(objectMap);
    }

    /**
     * Finds the value that will be checked given a specific object
     * @param object the to extract the value from
     * @return the string that is extracted
     */
    private static Object getValueFromObject(Object object) {
        if (((Map) object).get(ObjectMapKeys.OBJECT) instanceof JTextField) {
            return ((JTextField) ((Map) object).get(ObjectMapKeys.OBJECT)).getText();
        }
        if (((Map) object).get(ObjectMapKeys.OBJECT) instanceof JComboBox) {
            return ((JComboBox) ((Map) object).get(ObjectMapKeys.OBJECT)).getSelectedIndex();
        }
        return null;
    }

    /**
     * If the object is a JComponent then it will be check to see if it is enabled.
     * If it is not a JComponent it is just enabled.
     * @param objectMap to check
     * @return true if it is enabled.
     */
    private static boolean isObjectEnabled(Map objectMap) {
        if (isObjectFromObjectMapJComponent(objectMap)) {
            return ((JComponent) getObjectFromObjectMap(objectMap)).isEnabled();
        }
        return true;
    }

    /**
     * Will validate a string given a validation type.
     * @param value of the string to check
     * @param type of validation to use
     * @return true if the validation passes
     */
    public static boolean validateByType(Object value, int type, Map options) {
        if (type == INTEGER && isInteger(value)) {
            return true;
        }
        if (type == DOUBLE && isDouble(value)) {
            return true;
        }
        if (type == FILE_NAME && isValidFileName(value)) {
            return true;
        }
        if (type == NOT_EMPTY && isNotEmpty(value)) {
            return true;
        }
        if (type == COMPARE_VALUE && doesCompare(value, options)) {
            return true;
        }
        if (type == CUSTOM_METHOD && runCustomMethod(options)) {
            return true;
        }
        if (type == PATTERN_MATCH && matchToPatern(value, options)) {
            return true;
        }
        if (type == PERCENT && isPercent(value)) {
            return true;
        }
        return false;
    }

    /**
     * Set to true to display errors after validate
     * @return the displayErrorsAfterValidate
     */
    public boolean isDisplayErrorsAfterValidate() {
        return displayErrorsAfterValidate;
    }

    /**
     * Set to true to display errors after validate
     * @param displayErrorsAfterValidate the displayErrorsAfterValidate to set
     */
    public void setDisplayErrorsAfterValidate(boolean displayErrorsAfterValidate) {
        this.displayErrorsAfterValidate = displayErrorsAfterValidate;
    }

    ////////////////////////////////////////////////////////////////
    // VALIDATION METHODS
    ////////////////////////////////////////////////////////////////
    /**
     * Compares the given value with the mapped value. Map options are:
     * -- GREATER_THAN_VALUE - which is a double
     * -- LESS_THAN_VALUE - which is a double
     * -- EQUALS_VALUE - which is a double.
     * @param value to compare
     * @param options from the object
     * @return true if the validation passes
     */
    public static boolean doesCompare(Object value, Map options) {
        try {
            if (options.containsKey(OptionTypes.GREATER_THAN_VALUE)) {
                return (((Double) options.get(OptionTypes.GREATER_THAN_VALUE)) < (Double.parseDouble(value + "")));
            }
            if (options.containsKey(OptionTypes.LESS_THAN_VALUE)) {
                return (((Double) options.get(OptionTypes.LESS_THAN_VALUE)) > (Double.parseDouble(value + "")));
            }
            if (options.containsKey(OptionTypes.EQUALS_VALUE)) {
                return (((Double) options.get(OptionTypes.EQUALS_VALUE)) == (Double.parseDouble(value + "")));
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Will try to match the pattern with the value
     * @param value to match
     * @param options PATTERN_TO_MATCH
     * @return true if the pattern matches
     */
    public static boolean matchToPatern(Object value, Map options) {
        if (options.containsKey(OptionTypes.PATTERN_TO_MATCH)) {
            try {
                Pattern charPattern = Pattern.compile(((String) options.get(OptionTypes.PATTERN_TO_MATCH)));
                return charPattern.matcher((String) value).find();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check to see if a string value is a integer
     * @param value to check
     * @return true if the value is a integer
     */
    public static boolean isInteger(String value) {
        return isInteger((Object) value);
    }

    /**
     * Check to see if a string value is a integer
     * @param value to check
     * @return true if the value is a integer
     */
    public static boolean isInteger(Object value) {
        try {
            if (value instanceof Integer) {
                return true;
            } else {
                int i = Integer.parseInt((String) value);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Check to see if a string value is a double
     * @param value to check
     * @return true if the value is a double
     */
    public static boolean isDouble(String value) {
        return isDouble((Object) value);
    }

    /**
     * Check to see if a string value is a double
     * @param value to check
     * @return true if the value is a double
     */
    public static boolean isDouble(Object value) {
        try {
            if (value instanceof Double) {
                return true;
            } else {
                double d = Double.parseDouble((String) value);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Check to see if a value is a valid percent
     * @param value to check
     * @return true if the value is a percent
     */
    public static boolean isPercent(Object value) {
        double d;
        if (isDouble(value)) {
            d = Double.parseDouble((String) value);
            if (d >= 0.0 && d <= 100.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if a string is NOT empty
     * @param value the string to check
     * @return true if the string is not empty
     */
    public static boolean isNotEmpty(Object value) {
        return !((String) value).trim().isEmpty();
    }

    /**
     * Checks to see if the string is valid file name
     * @param value the name to check
     * @return true if it is a valid filename
     */
    public static boolean isValidFileName(Object value) {
        try {
            String sCharRegEx = "([^\\w \\-\\_\\.\\@])";
            Pattern pCharPattern = Pattern.compile(sCharRegEx);
            return !pCharPattern.matcher((String) value).find();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Runs a custom validation class that is in the class being validated
     * @param options
     * @return
     */
    public static boolean runCustomMethod(Map options) {
        if (options.containsKey(OptionTypes.CUSTOM_METHOD_NAME) && options.containsKey(OptionTypes.PARENT_CLASS)) {
            try {
                Class cls = ((Object) options.get(OptionTypes.PARENT_CLASS)).getClass();
                Method method = cls.getMethod((String) options.get(OptionTypes.CUSTOM_METHOD_NAME));
                return (Boolean) method.invoke((Object) options.get(OptionTypes.PARENT_CLASS));
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
