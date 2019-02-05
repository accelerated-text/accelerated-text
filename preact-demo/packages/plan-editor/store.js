export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
        documentPlan:       null,
        planName:           'Example Plan',
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
