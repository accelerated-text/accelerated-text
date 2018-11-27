import { h }            from 'preact';

import S                from './ABlock.sass';


const blockComponents = {
    SENTENCE:           require( './Sentence' ).default,
    TOKEN:              require( './Token' ).default,
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

    : (
        <div className={ S.unknownBlock }>
            Unknown block { block.type }!
        </div>
    );


export default ABlock;
