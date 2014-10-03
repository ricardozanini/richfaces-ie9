/**
 * 
 */
package org.richfaces.component;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.richfaces.validator.NullValueValidator;

/**
 * @author asmirnov
 *
 */
public abstract class UIRichInput extends UIInput {
	@Override
	protected void validateValue(FacesContext context, Object newValue) {
		// If our value is valid, enforce the required property if present
		if (isValid() && isRequired() && UIRichInput.isEmpty(newValue)) {
			super.validateValue(context, newValue);
		}
		UIRichInput.validateInput(context, this, newValue);

	}

	/**
	 * @param context
	 * @param newValue
	 */
	public static void validateInput(FacesContext context, UIInput component, Object newValue) {
		// If our value is valid and not empty, call all validators
		if (component.isValid()) {
			Validator[] validators = component.getValidators();
			if (validators != null) {
				for (Validator validator : validators) {
					try {
						if (validator instanceof NullValueValidator
								|| !isEmpty(newValue)) {
							validator.validate(context, component, newValue);
						}
					} catch (ValidatorException ve) {
						// If the validator throws an exception, we're
						// invalid, and we need to add a message
						component.setValid(false);
						FacesMessage message;
						String validatorMessageString = component.getValidatorMessage();

						if (null != validatorMessageString) {
							message = new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									validatorMessageString,
									validatorMessageString);
							message.setSeverity(FacesMessage.SEVERITY_ERROR);
						} else {
							message = ve.getFacesMessage();
						}
						if (message != null) {
							context.addMessage(component.getClientId(context), message);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean isEmpty(Object value) {

		if (value == null) {
			return (true);
		} else if ((value instanceof String) && (((String) value).length() < 1)) {
			return (true);
		} else if (value.getClass().isArray()) {
			if (0 == java.lang.reflect.Array.getLength(value)) {
				return (true);
			}
		} else if (value instanceof List) {
			if (((List) value).isEmpty()) {
				return (true);
			}
		}
		return (false);
	}

}
