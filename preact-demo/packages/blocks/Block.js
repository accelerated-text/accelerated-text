export default ({ init, json, type, ...spec }) =>
    Object.assign( Blockly => {

        const hasMutator = !!(
            spec.mutationToDom || spec.domToMutation
            || spec.compose || spec.decompose
            || spec.blockList
        );

        const mutator = (
            json.mutator
            || ( hasMutator && `${ type }_mutator` )
            || undefined
        );

        const hasExtension =    !!init;

        const extensions = (
            json.extensions
            || ( hasExtension && !hasMutator && [ `${ type }_extension` ])
            || undefined
        );

        const blockJson = {
            type,
            ...json,
            extensions,
            mutator,
        };

        Blockly.defineBlocksWithJsonArray([ blockJson ]);

        if( hasMutator ) {
            Blockly.Extensions.registerMutator( mutator, spec, init, spec.blockList );
        } else if( hasExtension ) {
            Blockly.Extensions.register( extensions[0], init );
        }
    }, { type });
