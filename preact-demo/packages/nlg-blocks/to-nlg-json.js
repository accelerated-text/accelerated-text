import {
    mapObjIndexed,
}   from 'ramda';

import getFieldMap      from '../blockly-helpers/get-field-map';
import getStatementMap  from '../blockly-helpers/get-statement-map';
import getValueMap      from '../blockly-helpers/get-value-map';


export const blockToJson = block => ({
    type:               block.type,
    blocklyId:          block.id,
});

export const fieldsToJson = block =>
    getFieldMap( block );

export const statementsToJson = block =>
    mapObjIndexed(
        blockList =>
            blockList.map(
                block => block.toNlgJson()
            ),
        getStatementMap( block ),
    );

export const valuesToJson = block =>
    mapObjIndexed(
        ( valueBlock, key ) =>
            valueBlock.toNlgJson(),
        getValueMap( block ),
    );


export default block => ({
    ...statementsToJson( block ),
    ...valuesToJson( block ),
    ...fieldsToJson( block ),
    ...blockToJson( block ),
});
