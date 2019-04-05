import fetchInject      from 'fetch-inject';


export default ({ language = 'en', prefix = '' }) =>
    window.Blockly
        ? Promise.resolve( window.Blockly )
        : (
            fetchInject([ `${ prefix }/blocks_compressed.js` ],
                fetchInject([ `${ prefix }/msg/js/${ language }.js` ],
                    fetchInject([ `${ prefix }/blockly_compressed.js` ])
                ),
            ).then(
                () => window.Blockly
            )
        );
