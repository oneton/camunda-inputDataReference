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
public class DmnCreateMultipleInputData {

  @Test
  public void shouldCreateMultipleInputData() {
	  Logger logger = LoggerFactory.getLogger(DmnCreateMultipleInputData.class);
	  
    DmnModelInstance modelInstance = Dmn.createEmptyModel();
	
    // Create a basic DMN model with definitions root element
	Definitions definitions = modelInstance.newInstance(Definitions.class);
	definitions.setName("InputData error reproduction");
	definitions.setId("InputDataErrorReproduction");
	definitions.setNamespace("http://anyschema.com/camunda");
	modelInstance.setDefinitions(definitions);
	
	// Add first input data
	InputData inputData = modelInstance.newInstance(InputData.class);
	inputData.setId("inputData1");
	inputData.setName("Input data 1");
	definitions.addChildElement(inputData);
	
	Variable variable = modelInstance.newInstance(Variable.class);
	variable.setId("inputData1Var");
	variable.setName("Input data 1");
	inputData.setInformationItem(variable);
	
	// Up to here things still work
	logger.info("Successfully created and added first inputData");
	
	// Try to add second input data
	modelInstance.newInstance(InputData.class);
	inputData.setId("inputData2");
	inputData.setName("Input data 2");
	
	logger.info("Successfully created instance of second inputData");
	
	// This causes an exception
	/**
	 * The message is rather confusing: New child is not a valid child element type: inputData; valid types are: [description, extensionElements, import, itemDefinition, drgElement, artifact, elementCollection, businessContextElement]
	 * ...because InputData is an instance of DrgElement.
	 * However: in the conversion from DOM to DMN model, the existing InputData becomes a InputDataReference instance (element names are the same). This is what causes the error.
	 */
	definitions.addChildElement(inputData);
	
	logger.info("Successfully added second inputData");
  }
  
}
