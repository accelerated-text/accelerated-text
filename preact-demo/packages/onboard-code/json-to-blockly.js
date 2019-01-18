const tokenToBlock = ({ partOfSpeech, text }) =>
    `<block type="quote">
        <field name="text">${ text }</field>
    </block>`;

const sentenceToBlock = ({ children }) =>
    `<block type="sequence">
        <mutation value_count="${ children.length }"/>
        ${ children.map(
            ( child, i ) =>
                `<value name="value_${ i }">
                    ${ child.children
                        ? sentenceToBlock( child )
                        : tokenToBlock( child ) }
                </value>`
        ).join( '' ) }
    </block>`;


export default sentences =>
    `<xml xmlns="http://www.w3.org/1999/xhtml">
        <block type="segment">
            <field name="text_type">description</field>
            <value name="items">
                ${ sentenceToBlock({ children: sentences }) }
            </value>
        </block>
    </xml>`;
