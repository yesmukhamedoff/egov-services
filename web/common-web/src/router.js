
import React from 'react';
import {Switch,Route} from 'react-router-dom';
import PropertyTaxSearch from './components/contents/PropertyTaxSearch';
import Login from './components/contents/Login';
import Dashboard from './components/contents/Dashboard';


const Main = () => (
  <main>
    <Switch>
      // <Route path='/propertyTaxSearch' component={PropertyTaxSearch}/>
      <Route exact path='/' component={Login}/>
      <Route exact path='/dashboard' component={Dashboard}/>    
    </Switch>
  </main>
)


export default(
  <Main/>
);
