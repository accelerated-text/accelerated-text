export default ({ init, json, type, ...mixin }) =>
    Object.assign(
        Blockly => {
            Blockly.Blocks[type] = {

                init() {
                    this.jsonInit( json );
                    this.mixin( mixin );
                    init && init.call( this );
                },
            };
        },
        { json, type, ...mixin },
    );
