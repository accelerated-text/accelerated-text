import { memoizeWith }      from 'ramda';


/// ( type → a ) → ( type → a )
export default memoizeWith( type => type.type );
