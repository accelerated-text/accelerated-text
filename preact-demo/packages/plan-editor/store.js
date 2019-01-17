export default {

    getInitialState: () => ({
        contextName:        null,
        dataSample:         null,
        planName:           'Example Plan',
        workspaceDom:       null,
        workspaceXml:       '',
    }),

    planEditor: {
        onChangeContext: ({ contextName }) => ({
            contextName,
        }),

        onChangeWorkspace: ({ workspaceDom, workspaceXml }) => ({
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
