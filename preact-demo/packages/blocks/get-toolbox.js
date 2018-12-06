export default blocks => ({

    registerBlocks( Blockly ) {

        blocks.forEach(
            block => block( Blockly )
        );
    },

    toXmlString() {
        return `
            <xml style="display: none">
                ${ blocks
                    .filter( block => !block.skipToolbox )
                    .map( block => `<block type="${ block.type }" />` )
                    .join( '' )
                }
            </xml>
        `;

    },
});
