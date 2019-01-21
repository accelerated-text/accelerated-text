import getFields        from './get-fields';

export default block =>
    getFields( block )
        .reduce(
            ( acc, field ) => {
                acc[field.name] = field.getValue();
                return acc;
            },
            {}
        );
