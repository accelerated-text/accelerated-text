import toNlgJson        from './to-nlg-json';

export default ({ type, ...spec }) =>
    Object.assign(
        Blockly => Blockly.Blocks[type] = {

            init() {
                const { init, json, ...mixin } = spec;

                this.jsonInit( json );

                const hasMutator = (
                    mixin.mutatorBlockList
                    && mixin.mutatorBlockList.length
                    && mixin.compose
                    && mixin.decompose
                );
                if( hasMutator ) {
                    this.setMutator( new Blockly.Mutator( mixin.mutatorBlockList ));
                }

                this.mixin({
                    toNlgJson() {
                        return toNlgJson( this );
                    },
                    ...mixin,
                });
                init && init.call( this );
            },
        },
        { type, ...spec },
    );
