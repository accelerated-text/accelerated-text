import Block                from './Block';
import valueList            from './value-list';


export default Block({

    ...valueList,

    type:                   'all',

    json: {
        ...valueList.json,

        colour:             202,
        output:             null,
        message0:           'all:',
    },
});
