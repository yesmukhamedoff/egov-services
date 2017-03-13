//getting current guerry strings
function getUrlVars() {
    var vars = [],
        hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

let months=["January", "February", "March", "April", "May",
 "June", "July", "August", "September", "October",
 "November", "December"];


class Attendance extends React.Component {
  constructor(props) {
    super(props);
    this.state={employees:{},attendance:[],month:"",year:"",code:"",designationCode:"",departmentCode:"",employeeType:"",startDate:undefined,endDate:undefined};
    this.handleCheckAll=this.handleCheckAll.bind(this);
    this.handleChange=this.handleChange.bind(this);
    this.markAttedance=this.markAttedance.bind(this);
    this.markBulkAttendance=this.markBulkAttendance.bind(this);
    this.save=this.save.bind(this);
  }

  save()
  {
      var employees=this.state.employees;
      var {month,year}=this.state;
      var attendance=[]
      for (var emp in employees) {
          for (var att in employees[emp].attendance) {
                // console.log(employees[emp].attendance[att].split("-")[1]);
                attendance.push({date:new Date(year,month,1),employee:emp,month,year,type:this.isValidType(employees[emp].attendance[att]),remarks:""});
          }
      }

      this.setState({
        attendance
      });
  }



  componentWillMount()
  {
    //call employee api and get employees
    // console.log(getUrlVars()["month"]);
    var queryParam=getUrlVars();
    // console.log(queryParam["type"]);
    var endDate=(parseInt(queryParam["year"])==new Date().getFullYear()&&parseInt(queryParam["month"])==new Date().getMonth())?new Date():new Date(parseInt(queryParam["year"]), parseInt(queryParam["month"])+1, 0);
    // var now = new Date();
    // var startDate=new Date(typeof(queryParam["year"])==="undefined"?now.getFullYear():parseInt(queryParam["year"]), typeof(queryParam["month"])==="undefined"?now.getMonth():parseInt(queryParam["month"]), 1);
    // console.log(typeof(queryParam["month"])==="undefined"?now.getMonth():parseInt(queryParam["month"]));
    var now = new Date();
    // var startDate=new Date((typeof(queryParam["year"])==="undefined")?now.getFullYear():parseInt(queryParam["year"]), (typeof(queryParam["month"])==="undefined")?now.getMonth():parseInt(queryParam["month"]), 1);
    // console.log(startDate);
    var employees={}
    // {
    //   "123":{
    //     name:"murali",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "124":{
    //     name:"Tharu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "125":{
    //     name:"mimmu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "126":{
    //     name:"murali",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "127":{
    //     name:"Tharu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "128":{
    //     name:"mimmu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "129":{
    //     name:"murali",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "130":{
    //     name:"Tharu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "131":{
    //     name:"mimmu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "132":{
    //     name:"murali",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "133":{
    //     name:"Tharu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "134":{
    //     name:"mimmu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "135":{
    //     name:"murali",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "136":{
    //     name:"Tharu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "137":{
    //     name:"mimmu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "138":{
    //     name:"murali",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "139":{
    //     name:"Tharu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   },
    //   "140":{
    //     name:"mimmu",
    //     month:"jun",
    //     year:"2017",
    //     attendance:{}
    //   }
    // }
    // var empTemp=[];
    for(var i=1;i<=300;i++)
    {
        employees[i]={
          name:"murali"+i,
          month:"jun",
          year:"2017",
          attendance:{}
        }
    }
    //merge employee with attendance

    for(var emp in employees)
    {
        // var daysOfYear = [];
        // console.log(Object.assign({}, startDate));
        var startDate=new Date((typeof(queryParam["year"])==="undefined")?now.getFullYear():parseInt(queryParam["year"]), (typeof(queryParam["month"])==="undefined")?now.getMonth():parseInt(queryParam["month"]), 1);
        for (var d = startDate ; d <= endDate; d.setDate(d.getDate() + 1)) {
        //  daysOfYear.push(new Date(d));
            employees[emp].attendance[`${d.getMonth()}-${d.getDate()}`]=(d.getDay()===0||d.getDay()===6)?"H":"";
            // console.log(employees[emp]);
        }

        // empTemp.push(emp);
    }

    this.setState({
        month:parseInt(queryParam["month"]),
        year:parseInt(queryParam["year"]),
        code:queryParam["code"],
        designationCode:queryParam["designationCode"],
        departmentCode:queryParam["departmentCode"],
        employeeType:queryParam["employeeType"],
        endDate,
        employees
    })
    //update calender
    // console.log(this.state.employees);
  }

  componentDidMount()
  {
    // $('#attendanceTable').DataTable({
    //   dom: 'Bfrtip',
    //   buttons: [
    //            'copy', 'csv', 'excel', 'pdf', 'print'
    //    ],
    //    ordering: true
    // });
  }

  componentDidUpdate(nextprops,nextState)
  {
    // $('#attendanceTable').DataTable({
    //   dom: 'Bfrtip',
    //   buttons: [
    //            'copy', 'csv', 'excel', 'pdf', 'print'
    //    ],
    //    ordering: true
    // });
  }

  close(){
      // widow.close();
      open(location, '_self').close();
  }

  handleCheckAll(e,date,type)
  {
      this.markBulkAttendance(date,type);
  }

  markBulkAttendance(oDate,type)
  {
    var employees=this.state.employees;
    var date=`${oDate.getMonth()}-${oDate.getDate()}`;
    for(var emp in employees)
    {
      var now = new Date();
      employees[emp].attendance[date]=type;
      // this.markAttedance(type,emp,date);
    }
    this.setState({
        employees:employees
    })
  }

  handleChange(e,empCode,date)
  {
    // console.log(e.target.value);
    // if (!this.isValidType(e.target.value)&&e.target.value!="") {
    //     alert("Pleae Enter Valid Type")
    //     return;
    // } else {
        this.markAttedance(e.target.value,empCode,date);
      // }
  }

  markAttedance(value,empCode,date)
  {

      this.setState({
        employees:{
            ...this.state.employees,
            [empCode]:{
              ...this.state.employees[empCode],
              ["attendance"]:{
                  ...this.state.employees[empCode]["attendance"],
                  [date]:value
              }
            }
          }
      })

  }

  isValidType(value)
  {
      switch (value) {
        case "P":
          return "Present";
          break;
        case "A":
          return "Absent";
          break;
        case "CO":
          return "CompOff";
          break;
        case "L":
          return "LeavePaid";
          break;
        case "HP":
          return "HalfPresent";
          break;
        case "HLP":
          return "HalfLeavePaid";
          break;
        case "THLP":
          return "TwoHalfLeavesPaid ";
          break;
        case "LU":
          return "LeaveUnpaid";
          break;
        case "HLU":
          return "HalfLeaveUnpaid";
          break;
        case "THLU":
          return "TwoHalfLeavesUnpaid";
          break;
        case "OT":
          return "OverTime";
          break;
        case "CE":
          return "CompOffElligibe";
          break;
        case "H":
          return "Holiday";
          break;
        default:
          return "";
          break;
      }
  }



  render() {

    console.log(this.state);
    let {month,year,designationCode,departmentCode,code,type,endDate}=this.state;
  //  console.log(startDate);
    let {handleCheckAll,handleChange,save}=this;
    const showEmployee=function(employees)
    {
        return Object.keys(employees).map((k, index)=>
        {
          return (<tr key={employees[k].code}>
                      <td>{index+1}</td>
                      <td>{k+"-"+employees[k].name}</td>
                      {createCalender("td",k,employees[k].attendance)}
                  </tr>)
        })

    }
    const getDay=function(day)
    {
      switch (day) {
        case 0:return "Sun";
          break;
        case 1:return "Mon";
          break;
        case 2:return "Tue";
          break;
        case 3:return "Wed";
          break;
        case 4:return "Thu";
          break;
        case 5:return "Fri";
          break;
        default:return "Sat";
          break;
      }
    }
    const getColor=function(type)
    {
        switch (type) {
          case "P":
            return "inputBoxGreen"
            break;
          case "H":
            return "inputBoxWarning"
            break;
          case "A":
            return "inputBoxRed"
            break;
          default:
            return ""
            break;

        }
    }
    const createCalender=function(type="th",empCode="",attendance={})
    {
        // console.log("emp code is " + empCode);
        var now = new Date();
        var daysOfYear = [];
        // var endDate=
        // console.log(now.getFullYear());
        // console.log(month);
        var startDate=new Date((typeof(year)==="undefined")?now.getFullYear():year, (typeof(month)==="undefined")?now.getMonth():month, 1);
        // console.log(new Date(typeof(year)==="undefined"?now.getFullYear():year, typeof(month)?now.getMonth():month, 1));
        for (var d = startDate; d <= endDate; d.setDate(d.getDate() + 1)) {
            daysOfYear.push(new Date(d));
        }
        if(type=="th"&&daysOfYear.length>0)
        {

          return daysOfYear.map(function(item,index) {
                return (<th key={index} className="text-center">
                      {item.getDate()}
                      <br/>
                      {getDay(item.getDay())}
                      <div className="checkbox">
                          <label>
                            <input type="checkbox" onChange={(e)=>{handleCheckAll(e,item,e.target.checked?"P":"")}}/>
                          </label>
                      </div>


                  </th>);
          })
        }
        else if (daysOfYear.length>0) {
          return daysOfYear.map(function(item,index) {
                var date=`${item.getMonth()}-${item.getDate()}`;
                return (<td key={index} className="text-center">
                            <input type="text" className={`form-control ${getColor(typeof(attendance[date])==="undefined"?"":attendance[date])}`} style={{width: "44px",fontSize: "10px",textAlign: "center"}}  value={typeof(attendance[date])==="undefined"?"":attendance[date]} onChange={(e)=>{
                                handleChange(e,empCode,date)
                            }}/>
                        </td>);
                      })
        }

    };
    return (
      <div>
          <h3>Attendance for the month of : {months[month]}-{year} </h3>
          <div className="attendance-table table-responsive">
              <table className="table table-bordered" id="attendanceTable">
                  <thead>

                      <tr>
                          <th>Sl.No</th>
                          <th>Name</th>
                          {createCalender()}
                      </tr>
                  </thead>

                  <tbody id="attendanceTableBody">
                    {showEmployee(this.state.employees)}
                  </tbody>

              </table>

          </div>


            <div className="text-center">
                <button type="button" className="btn btn-submit" onClick={(e)=>{this.close()}}>Close</button>
                <button type="button" id="addEmployee" className="btn btn-submit" onClick={(e)=>{save()}}>Save</button>
            </div>



      </div>
    );
  }
}



ReactDOM.render(
  <Attendance />,
  document.getElementById('root')
);


// <div className="dropdown">
//     <button className="btn btn-success dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown"  aria-expanded="true">
//       P
//       <span className="caret"></span>
//     </button>
//     <ul className="dropdown-menu" aria-labelledby="dropdownMenu1">
//       <li><a className="inputBoxGreen" href="#">P</a></li>
//       <li><a className="inputBoxRed" href="#">A</a></li>
//       <li><a className="inputBoxWarning" href="#">H</a></li>
//     </ul>
//   </div>
