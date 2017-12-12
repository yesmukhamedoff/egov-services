var dat = {
  'inventory.search': {
    numCols: 4,
    useTimestamp: true,
    objectName: '',
    url: '/inventory-services/materialstoremapping/_search',
    groups: [
      {
        name: 'search',
        label: 'inventory.search.title',
        fields: [
          {
            name: 'material',
            pattern: '',
            label: 'inventory.label.material',
            type: 'autoCompelete',
            jsonPath: 'material',
            displayJsonPath: 'materialName',
            isRequired: false,
            isDisabled: false,
            url:
              '/egov-mdms-service/v1/_get?&moduleName=inventory&masterName=Material|$.MdmsRes.inventory.Material[*].code|$.MdmsRes.inventory.Material[*].name',
          },

          {
            name: 'store',
            pattern: '',
            label: 'inventory.label.store',
            type: 'singleValueList',
            jsonPath: 'store',
            isRequired: false,
            isDisabled: false,
            url:
              'inventory-services/stores/_search?|$.stores[*].code|$.stores[*].name',
          },
          {
            name: 'isActive',
            jsonPath: 'active',
            label: 'inventory.label.isactive',
            type: 'checkbox',
            defaultValue: true,
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
        ],
      },
    ],
    result: {
      header: [
        {
          label: 'inventory.search.result.Material Name',
        },
        {
          label: 'inventory.search.result.Material Type Name',
        },
        {
          label: 'inventory.search.result.Dtore',
        },
      ],
      values: [
        'material.code',
        'store.name',
        { valuePath: 'active', type: 'checkbox' },
      ],
      resultPath: 'materialStoreMappings',
      resultIdKey: 'id',
      rowClickUrlUpdate: '/update/inventory/materialstoremapping/{code}',
      rowClickUrlView: '/view/inventory/materialstoremapping/{code}',
      rowClickUrlAdd: '/create/inventory/materialstoremapping',
      rowClickUrlDelete: '',
    },
  },
  'inventory.create': {
    numCols: 4,
    useTimestamp: true,
    objectName: 'materialStoreMappings',
    groups: [
      {
        name: 'Material Map To Store',
        label: 'inventory.create.group.title.Material Map To Store',
        fields: [
          {
            name: 'department',
            pattern: '',
            type: 'singleValueList',
            jsonPath: 'departmentMaster',
            isRequired: false,
            isDisabled: false,
            hide: true,
            url:
              '/egov-mdms-service/v1/_get?&moduleName=common-masters&masterName=Department|$..code|$..name',
          },

          {
            type: 'tableList',
            jsonPath: '',
            tableList: {
              header: [
                {
                  label: 'Material',
                },
                {
                  label: 'Store Name',
                },
                {
                  label: 'Department Name',
                },
                {
                  label: 'Account Code',
                },
                {
                  label: 'Active',
                },
              ],
              values: [
                {
                  name: 'material',
                  pattern: '',
                  type: 'autoCompelete',
                  jsonPath: 'materialStoreMappings[0].material.code',
                  displayJsonPath: 'materialStoreMappings[0].material.name',
                  isRequired: true,
                  isDisabled: false,
                  url:
                    '/egov-mdms-service/v1/_get?&moduleName=inventory&masterName=Material|$.MdmsRes.inventory.Material[*].code|$.MdmsRes.inventory.Material[*].name',
                },
                {
                  name: 'store',
                  pattern: '',
                  type: 'singleValueList',
                  jsonPath: 'materialStoreMappings[0].store.code',
                  isRequired: true,
                  isDisabled: false,
                  url:
                    'inventory-services/stores/_search?|$.stores[*].code|$.stores[*].name|$.stores[*].department',
                  depedants: [
                    {
                      jsonPath: 'materialStoreMappings[0].department.code',
                      type: 'textField',
                      valExp:
                        "getValFromDropdownData('materialStoreMappings[*].store.code', getVal('materialStoreMappings[*].store.code'), 'others[0].code')",
                    },
                    {
                      jsonPath: 'materialStoreMappings[0].department.name',
                      type: 'textField',
                      valExp:
                        "getValFromDropdownData('departmentMaster', getVal('materialStoreMappings[*].department.code'), 'value')",
                    },
                  ],
                },
                {
                  name: 'department',
                  pattern: '',
                  type: 'text',
                  jsonPath: 'materialStoreMappings[0].department.name',
                  isRequired: true,
                  isDisabled: true,
                },
                {
                  name: 'accountcode',
                  pattern: '',
                  type: 'singleValueList',
                  jsonPath: 'materialStoreMappings[0].chartofAccount.glCode',
                  isRequired: true,
                  isDisabled: false,
                  url:
                    '/egf-master/chartofaccounts/_search?|$.chartOfAccounts[*].glcode|$.chartOfAccounts[*].name',
                },
                {
                  name: 'active',
                  pattern: '',
                  type: 'checkbox',
                  defaultValue: true,
                  label: '',
                  jsonPath: 'materialStoreMappings[0].active',
                  isRequired: false,
                  isDisabled: false,
                },
              ],
            },
          },
        ],
      },
    ],
    url: '/inventory-services/materials/_create',
    tenantIdRequired: true,
  },
  'inventory.view': {
    numCols: 4,
    useTimestamp: true,
    objectName: 'materials',
    groups: [
      {
        name: 'Material Map To Store',
        label: 'inventory.create.group.title.Material Map To Store',
        fields: [
          {
            type: 'tableList',
            jsonPath: '',
            tableList: {
              header: [
                {
                  label: 'Material',
                },
                {
                  label: 'Store Name',
                },
                {
                  label: 'Department Name',
                },
                {
                  label: 'Account Code',
                },
                {
                  label: 'Active',
                },
              ],
              values: [
                {
                  name: 'material',
                  pattern: '',
                  type: 'autoCompelete',
                  jsonPath: 'materialStoreMappings[0].material.code',
                  isRequired: true,
                  isDisabled: false,
                  url:
                    '/egov-mdms-service/v1/_get?&moduleName=inventory&masterName=Material|$.MdmsRes.inventory.Material[*].code|$.MdmsRes.inventory.Material[*].name',
                },
                {
                  name: 'store',
                  pattern: '',
                  type: 'singleValueList',
                  jsonPath: 'materialStoreMappings[0].store.code',
                  isRequired: true,
                  isDisabled: false,
                  url:
                    'inventory-services/stores/_search?|$.stores[*].code|$.stores[*].name|$.stores[*].department',
                  depedants: [
                    {
                      jsonPath: 'materialStoreMappings[0].department.name',
                      type: 'textField',
                      valExp:
                        "getValFromDropdownData('materialStoreMappings[*].store.code', getVal('materialStoreMappings[*].store.code'), 'others.code')",
                    },
                  ],
                },
                {
                  name: 'department',
                  pattern: '',
                  type: 'text',
                  jsonPath: 'materialStoreMappings[0].department.name',
                  isRequired: true,
                  isDisabled: true,
                },
                {
                  name: 'accountcode',
                  pattern: '',
                  type: 'singleValueList',
                  jsonPath: 'materialStoreMappings[0].chartofAccount.glCode',
                  isRequired: true,
                  isDisabled: false,
                  url:
                    '/egf-master/chartofaccounts/_search?|$.chartOfAccounts[*].glcode|$.chartOfAccounts[*].name',
                },
                {
                  name: 'active',
                  pattern: '',
                  type: 'checkbox',
                  defaultValue: true,
                  label: '',
                  jsonPath: 'materialStoreMappings[0].active',
                  isRequired: false,
                  isDisabled: false,
                },
              ],
            },
          },
        ],
      },
    ],
    tenantIdRequired: true,
    url: '/inventory-services/materialstoremapping/_search?tenantId={tenantId}',
  },
  'inventory.update': {
    numCols: 4,
    useTimestamp: true,
    objectName: 'materials',
    groups: [
      {
        name: 'Add Material',
        label: 'inventory.create.group.title.Add Material',
        fields: [
          {
            name: 'code',
            jsonPath: 'materials[0].code',
            label: 'inventory.create.code',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            maxLength: 50,
            minLength: 5,
            patternErrorMsg: '',
          },
          {
            name: 'oldCode',
            jsonPath: 'materials[0].oldCode',
            label: 'inventory.create.oldCode',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            maxLength: 50,
            patternErrorMsg: '',
          },
          {
            name: 'code',
            jsonPath: 'materials[0].materialType.code',
            label: 'inventory.create.code',
            type: 'singleValueList',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
            url:
              '/egov-mdms-service/v1/_get?&moduleName=inventory&masterName=MaterialType|$..code|$..name',
          },
          {
            name: 'name',
            jsonPath: 'materials[0].name',
            label: 'inventory.create.name',
            type: 'text',
            isRequired: true,
            isDisabled: false,
            maxLength: 50,
            minLength: 5,
            patternErrorMsg: '',
          },
          {
            name: 'description',
            jsonPath: 'materials[0].description',
            label: 'inventory.create.description',
            type: 'textarea',
            isRequired: true,
            isDisabled: false,
            maxLength: 1000,
            patternErrorMsg: '',
          },
          {
            name: 'name',
            jsonPath: 'materials[0].baseUom.name',
            label: 'inventory.create.name',
            type: 'singleValueList',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'inventoryType',
            jsonPath: 'materials[0].inventoryType',
            label: 'inventory.create.inventoryType',
            type: 'singleValueList',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'status',
            jsonPath: 'materials[0].status',
            label: 'inventory.create.status',
            type: 'singleValueList',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
        ],
      },
      {
        name: 'Puchasing Information',
        label: 'inventory.create.group.title.Puchasing Information',
        fields: [
          {
            name: 'code',
            jsonPath: 'materials[0].purchaseUom.code',
            label: 'inventory.create.code',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
            url:
              '/egov-mdms-service/v1/_get?&moduleName=common-masters&masterName=Uom|$..code|$..code',
          },
          {
            name: 'glCode',
            jsonPath: 'materials[0].expenseAccount.glCode',
            label: 'inventory.create.glCode',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
        ],
      },
      {
        name: 'Stocking Information',
        label: 'inventory.create.group.title.Stocking Information',
        fields: [
          {
            name: 'materialClass',
            jsonPath: 'materials[0].materialClass',
            label: 'inventory.create.materialClass',
            type: 'singleValueList',
            isRequired: true,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'name',
            jsonPath: 'materials[0].staockingUom.name',
            label: 'inventory.create.name',
            type: 'singleValueList',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'minQuantity',
            jsonPath: 'materials[0].minQuantity',
            label: 'inventory.create.minQuantity',
            type: 'number',
            isRequired: true,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'maxQuantity',
            jsonPath: 'materials[0].maxQuantity',
            label: 'inventory.create.maxQuantity',
            type: 'number',
            isRequired: true,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'reorderLevel',
            jsonPath: 'materials[0].reorderLevel',
            label: 'inventory.create.reorderLevel',
            type: 'number',
            isRequired: true,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'reorderQuantity',
            jsonPath: 'materials[0].reorderQuantity',
            label: 'inventory.create.reorderQuantity',
            type: 'number',
            isRequired: true,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'materialControlType',
            jsonPath: 'materials[0].materialControlType',
            label: 'inventory.create.materialControlType',
            type: 'singleValueList',
            isRequired: true,
            isDisabled: false,
            patternErrorMsg: '',
          },
        ],
      },
      {
        name: 'Specification',
        label: 'inventory.create.group.title.Specification',
        fields: [
          {
            name: 'model',
            jsonPath: 'materials[0].model',
            label: 'inventory.create.model',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'manufacturePartNo',
            jsonPath: 'materials[0].manufacturePartNo',
            label: 'inventory.create.manufacturePartNo',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'techincalSpecs',
            jsonPath: 'materials[0].techincalSpecs',
            label: 'inventory.create.techincalSpecs',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
          {
            name: 'termsOfDelivery',
            jsonPath: 'materials[0].termsOfDelivery',
            label: 'inventory.create.termsOfDelivery',
            type: 'text',
            isRequired: false,
            isDisabled: false,
            patternErrorMsg: '',
          },
        ],
      },
    ],
    url: '/inventory-services/materialstoremapping/_update',
    tenantIdRequired: true,
    searchUrl:
      '/inventory-services/materialstoremapping/_search?tenantId={tenantId}',
  },
};
export default dat;
