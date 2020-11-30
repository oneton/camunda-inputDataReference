/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.unittest;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.InputData;
import org.camunda.bpm.model.dmn.instance.Variable;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Tieleman
 */
public class DmnCreateMultipleInputDataTestCase {

	private static final Logger logger = LoggerFactory.getLogger(DmnCreateMultipleInputDataTestCase.class);


	private DmnModelInstance createModel() {
		DmnModelInstance modelInstance = Dmn.createEmptyModel();

		// Create a basic DMN model with definitions root element
		Definitions definitions = modelInstance.newInstance(Definitions.class);
		definitions.setName("InputData error reproduction");
		definitions.setId("InputDataErrorReproduction");
		definitions.setNamespace("http://anyschema.com/camunda");
		modelInstance.setDefinitions(definitions);
		
		return modelInstance;
	}
	
	private InputData createInputData(DmnModelInstance modelInstance, int index) {
		String name = String.format("Input data %d", index);
		
		InputData inputData = modelInstance.newInstance(InputData.class, String.format("inputData%d", index));
		inputData.setName(name);
		
		Variable variable = modelInstance.newInstance(Variable.class, String.format("inputDataVar%d", index));
		variable.setName(name);
		inputData.setInformationItem(variable);
		
		return inputData;
	}

	@Test
	public void shouldCreateMultipleInputData() {
		DmnModelInstance modelInstance = createModel();
		Definitions definitions = modelInstance.getDefinitions();

		// Add first input data
		InputData inputData = createInputData(modelInstance, 1);
		definitions.addChildElement(inputData);

		// Create second input data
		inputData = createInputData(modelInstance, 2);

		logger.info("shouldCreateMultipleInputData: Successfully created instance of second inputData");

		/**
		 * This causes an exception when called the second time
		 * 
		 * The message is rather confusing: New child is not a valid child element type: inputData; valid types are: [description, extensionElements, import, itemDefinition, drgElement, artifact, elementCollection, businessContextElement]
		 * ...because InputData is an instance of DrgElement.
		 * However: in the conversion from DOM to DMN model, the existing InputData becomes a InputDataReference instance (element names are the same). This is what causes the error.
		 */
		definitions.addChildElement(inputData);

		logger.info("shouldCreateMultipleInputData: Successfully added second inputData");
	}

	@Test
	public void shouldCreateMultipleInputDataWorkaround() {
		DmnModelInstance modelInstance = createModel();
		Definitions definitions = modelInstance.getDefinitions();

		// Add first input data
		InputData inputData = createInputData(modelInstance, 1);
		definitions.addChildElement(inputData);

		// Workaround: the model element instance of the DOM node doesn't need to be created in this case
		if (inputData.getDomElement().getModelElementInstance() == null) {
			inputData.getDomElement().setModelElementInstance(inputData);
		}

		// Create second input data
		inputData = createInputData(modelInstance, 2);

		definitions.addChildElement(inputData);

		logger.info("shouldCreateMultipleInputDataWorkaround: Successfully added second inputData");
	}
}
