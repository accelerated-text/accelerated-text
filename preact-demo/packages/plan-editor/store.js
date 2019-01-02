import domToGremlin         from '../blockly-gremlin/dom-to-gremlin';


export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
        gremlinCode:        '',
        planName:           'Example Plan',
        workspaceDom:       null,
        workspaceXml:       '',
    }),

    planEditor: {
        onChangeContext: ({ contextName }) => ({
            contextName,
        }),

        onChangeGremlinCode: gremlinCode => ({
            gremlinCode,
        }),

        onChangeWorkspace: ({ workspaceDom, workspaceXml }) => ({
            gremlinCode:    domToGremlin( workspaceDom ),
            workspaceDom,
            workspaceXml,
        }),

        onCreateWorkspaceXml: workspaceXml => ({
            workspaceXml,
        }),

        onClickUpload: ({ dataSample }) => ({
            dataSample,
        }),
    },
};
