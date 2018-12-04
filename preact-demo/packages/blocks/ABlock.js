import { h }            from 'preact';

import S                from './ABlock.sass';

import Sentence         from './Sentence';
import Token            from './Token';


const blockComponents = {
    ATTRIBUTE:          Token,
    CARDINAL:           Token,
    ORG:                Token,
    SEGMENT:            Sentence,
    SENTENCE:           Sentence,
    TOKEN:              Token,
};

const ABlock = ({ block }) =>

    ( !block )
        ? null

    : block instanceof Array
        ? <div className={ S.list }>
            { block.map( block => <ABlock block={ block } /> )}
        </div>

    : blockComponents[block.type]
        ? blockComponents[block.type]({ block })

    : block.text
        ? <Token block={ block } />

    : ( block.children && block.children.length )
        ? <Sentence block={ block } />

    : null;

export default ABlock;
