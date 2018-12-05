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
        ).join( '' ) }
        ${ result.next
            ? `<next>${ sentenceToBlock( result.next ) }</next>`
            : ''
        }
    </block>`;

const arrayToLinkedList = ( acc, item, i ) => {

    if( i === 1 ) {
        acc.next =          item;
        return {
            head:           acc,
            previous:       item,
        };
    } else {
        acc.previous.next = item;
        acc.previous =      item;
        return acc;
    }
};


export default sentences =>
    `<xml xmlns="http://www.w3.org/1999/xhtml">
        <block type="segment">
            <field name="GOAL">description</field>
            <statement name="CHILDREN">
                ${ sentenceToBlock(
                    sentences.reduce( arrayToLinkedList ).head
                )}
            </statement>
        </block>
    </xml>`;
