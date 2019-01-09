const tokenToBlock = ({ partOfSpeech, text }) =>
    `<block type="token">
        <field name="part_of_speech">${ partOfSpeech }</field>
        <field name="text">${ text }</field>
    </block>`;

const sentenceToBlock = ({ children, next }) =>
    `<block type="sentence">
        <mutation value_count="${ children.length }"/>
        ${ children.map(
            ( child, i ) =>
                `<value name="value_${ i }">${ tokenToBlock( child ) }</value>`
        ).join( '' ) }
        ${ next
            ? `<next>${ sentenceToBlock( next ) }</next>`
            : ''
        }
    </block>`;


export default sentences =>
    `<xml xmlns="http://www.w3.org/1999/xhtml">
        <block type="segment">
            <field name="text_type">description</field>
            <value name="items">
                <block type="all-words">
                    <mutation value_count="${ sentences.length }"/>
                    ${ sentences.map(
                        ( sentence, i ) =>
                            `<value name="value_${ i }">
                                ${ sentenceToBlock( sentence ) }
                            </value>`
                    )}
                </block>
            </value>
        </block>
    </xml>`;
