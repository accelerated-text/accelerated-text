export default ( files, plan ) => (
    files && files.length
    && plan && plan.dataSampleId
    && files.find(({ id }) => id === plan.dataSampleId )
);
