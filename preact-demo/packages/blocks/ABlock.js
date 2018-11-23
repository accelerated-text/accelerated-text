import { h, Component } from 'preact';

import S from './ABlock.sass';

const blocks = {
    word:               require( './Word' ).default,
};

export default function ABlock({ block }) {

    if( !block ){
        return null;

    } else if( block instanceof Array ){
        return (
            <div className={ S.list }>
                { block.map( block => ABlock({ block })) }
            </div>
        );
    } else if( blocks[block.type] ){
        return blocks[block.type]({ block });

    } else {
        return (
            <div className={ S.unknownBlock }>
                Unknown block { block.type }!
            </div>
        );
    }
}
