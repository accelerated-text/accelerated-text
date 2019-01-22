export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
        documentPlan:       null,
        workspaceXml:       '',
    }),

    planEditor: {
        onChangeContext: ({ contextName }) => ({
            contextName,
        }),

        onChangeWorkspace: ({ documentPlan, workspaceDom, workspaceXml }) => ({
            documentPlan,
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
