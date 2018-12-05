const tokenToBlock = result =>
    `<block type="token">
        <field name="part_of_speech">${ result.partOfSpeech }</field>
        <field name="text">${ result.text }</field>
    </block>`;

const sentenceToBlock = result =>
    `<block type="sentence">
        <mutation children_count="${ result.children.length }"/>
        ${ result.children.map(
            ( child, i ) =>
                `<value name="CHILD${ i }">${ tokenToBlock( child ) }</value>`
        ).join( '' )}
    </block>`;

const resultToBlock = result =>
    ( result.type === 'SENTENCE' || result.children )
        ? sentenceToBlock( result )
    : ( result.type === 'TOKEN' || result.text )
        ? tokenToBlock( result )
    : '';


export default results =>
    `<xml xmlns="http://www.w3.org/1999/xhtml">
        ${ results.map( resultToBlock ).join( '' ) }
    </xml>`;
