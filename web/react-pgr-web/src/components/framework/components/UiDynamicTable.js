import React, {Component} from 'react';
import {connect} from 'react-redux';

import {Grid, Row, Col, Table, DropdownButton} from 'react-bootstrap';
import {Card, CardHeader, CardText} from 'material-ui/Card';
import UiTextField from './components/UiTextField'
import UiSelectField from './components/UiSelectField'
import UiButton from './components/UiButton'
import UiEmailField from './components/UiEmailField'
import UiMobileNumber from './components/UiMobileNumber'
import UiTextArea from './components/UiTextArea'
import UiMultiSelectField from './components/UiMultiSelectField'
import UiNumberField from './components/UiNumberField'
import UiDatePicker from './components/UiDatePicker'
import UiMultiFileUpload from './components/UiMultiFileUpload'
import UiSingleFileUpload from './components/UiSingleFileUpload'
import UiAadharCard from './components/UiAadharCard'
import UiPanCard from './components/UiPanCard'

class UiDynamicTable extends Component {
	constructor(props) {
       super(props);
   	}

   	render() {
   		let self = this;
   		const renderFields = function(field) {
   			switch(field.type) {
   				case 'text':
   					return (
   						<UiTextField ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'singleValueList':
   					return (
   						<UiSelectField ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'multiValueList':
   					return (
   						<UiMultiSelectField ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'number':
   					return (
   						<UiNumberField ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'textarea':
   					return (
   						<UiTextArea ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'mobileNumber':
   					return (
   						<UiMobileNumber ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'email':
   					return (
   						<UiEmailField ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'datePicker':
   					return (
   						<UiDatePicker ui="google" getVal={self.props.getVal} item={field}  fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					);
   				case 'pan':
			        return (
			        	<UiPanCard ui="google" getVal={self.props.getVal} item={field} fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
			    	)
			    case 'aadhar':
			        return (
			        	<UiAadharCard ui="google" getVal={self.props.getVal} item={field} fieldErrors={self.props.fieldErrors} handler={self.props.handler}/>
   					)
   				case 'table':
   					renderTable(field);
   			}
   		}

   		const renderTable = function(item) {
   			return (
   				<Card>
			      <CardHeader title={<strong> {item.label} </strong>}/>
			        <CardText>
			          <Table className="dynamicTable" bordered responsive>
				          <thead>
				            <tr>
				              {item.header && item.header.length && item.header.map((item, i) => {
				                return (
				                  <th key={i}>{translate(item.label)}</th>
				                )
				              })}
				            </tr>
				          </thead>
				          <tbody>
				                {
				                	item.values.map((item2, i2) => {
					                  return (
					                    <tr key={i2}>
					                      <td>{renderFields(item2)}</td>
					                    </tr>
					                   )
				                	})
				                }
				          </tbody>
			          </Table>
			      	</CardText>
			    </Card>
   			)
   		}

   		return (
   			{renderTable(self.props.item)}
   		)
   	}
}