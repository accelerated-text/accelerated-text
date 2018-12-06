const arrayToLinkedList = arr =>
    arr.reduce(( acc, item ) => {
        if( !acc.head ) {
            acc.head =          item;
        } else {
            acc.previous.next = item;
        }
        acc.previous =          item;
        return acc;
    }, {}).head;

const tokenToBlock = ({ partOfSpeech, text }) =>
    `<block type="token">
        <field name="part_of_speech">${ partOfSpeech }</field>
        <field name="text">${ text }</field>
    </block>`;

const sentenceToBlock = ({ children, next }) =>
    `<block type="sentence">
        <mutation children_count="${ children.length }"/>
        ${ children.map(
            ( child, i ) =>
                `<value name="CHILD${ i }">${ tokenToBlock( child ) }</value>`
        ).join( '' ) }
        ${ next
            ? `<next>${ sentenceToBlock( next ) }</next>`
            : ''
        }
    </block>`;


export default sentences =>
    `<xml xmlns="http://www.w3.org/1999/xhtml">
        <block type="segment">
            <field name="GOAL">description</field>
            <statement name="CHILDREN">
                ${ sentenceToBlock( arrayToLinkedList( sentences )) }
            </statement>
        </block>
    </xml>`;
