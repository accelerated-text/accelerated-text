export default ( spec ) =>
    Object.assign(
        Blockly => Blockly.Blocks[spec.type] = {

            init() {

                const { init, json, ...mixin } = spec;

                this.jsonInit( json );
                this.mixin( mixin );
                init && init.call( this );
            },
        },
        spec,
    );
