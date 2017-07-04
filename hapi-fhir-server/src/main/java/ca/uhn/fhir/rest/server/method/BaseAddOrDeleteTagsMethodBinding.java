package ca.uhn.fhir.rest.server.method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TagList;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.TagListParam;
import ca.uhn.fhir.rest.api.*;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.ParameterUtil;
import ca.uhn.fhir.rest.server.IResourceProvider;

abstract class BaseAddOrDeleteTagsMethodBinding extends BaseMethodBinding<Void> {

	private Class<? extends IBaseResource> myType;
	private Integer myIdParamIndex;
	private Integer myVersionIdParamIndex;
	private String myResourceName;
	private Integer myTagListParamIndex;

	public BaseAddOrDeleteTagsMethodBinding(Method theMethod, FhirContext theContext, Object theProvider, Class<? extends IBaseResource> theTypeFromMethodAnnotation) {
		super(theMethod, theContext, theProvider);

		if (theProvider instanceof IResourceProvider) {
			myType = ((IResourceProvider) theProvider).getResourceType();
		} else {
			myType = theTypeFromMethodAnnotation;
		}

		if (Modifier.isInterface(myType.getModifiers())) {
			throw new ConfigurationException("Method '" + theMethod.getName() + "' does not specify a resource type, but has an @" + IdParam.class.getSimpleName()
					+ " parameter. Please specity a resource type in the method annotation on this method");
		}

		myResourceName = theContext.getResourceDefinition(myType).getName();

		myIdParamIndex = ParameterUtil.findIdParameterIndex(theMethod, getContext());
		myVersionIdParamIndex = ParameterUtil.findVersionIdParameterIndex(theMethod);
		myTagListParamIndex = ParameterUtil.findTagListParameterIndex(theMethod);

		if (myIdParamIndex == null) {
			throw new ConfigurationException("Method '" + theMethod.getName() + "' does not have an @" + IdParam.class.getSimpleName() + " parameter.");
		}

		if (myTagListParamIndex == null) {
			throw new ConfigurationException("Method '" + theMethod.getName() + "' does not have a parameter of type " + TagList.class.getSimpleName() + ", or paramater is not annotated with the @"
					+ TagListParam.class.getSimpleName() + " annotation");
		}

	}

	@Override
	public String getResourceName() {
		return myResourceName;
	}

	@Override
	public RestOperationTypeEnum getRestOperationType() {
		return null;
	}

	protected abstract boolean isDelete();

	@Override
	public boolean incomingServerRequestMatchesMethod(RequestDetails theRequest) {
		if (theRequest.getRequestType() != RequestTypeEnum.POST) {
			return false;
		}
		if (!Constants.PARAM_TAGS.equals(theRequest.getOperation())) {
			return false;
		}

		if (!myResourceName.equals(theRequest.getResourceName())) {
			return false;
		}

		if (theRequest.getId() == null) {
			return false;
		}

		if (isDelete()) {
			if (Constants.PARAM_DELETE.equals(theRequest.getSecondaryOperation()) == false) {
				return false;
			}
		} else {
			if (theRequest.getSecondaryOperation() != null) {
				return false;
			}
		}

		return true;
	}

}
