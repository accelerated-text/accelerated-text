const { resultId } =    require( './nlg-job' );
const VARIANT =         require( './variant' );

module.exports = {
    key:        resultId,
    ready:      true,
    variants:   [ VARIANT ],
    updatedAt:  +new Date,
};
