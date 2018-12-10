export default ({ type, ...spec }) =>
    Object.assign(
        Blockly => Blockly.Blocks[type] = {

            init() {
                const { init, json, ...mixin } = spec;

                this.jsonInit( json );
                this.mixin( mixin );
                init && init.call( this );
            },
        },
        { type, ...spec },
    );
