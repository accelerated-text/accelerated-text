import debounce             from 'lodash.debounce';
import withDebouncedProps   from 'react-debounced-props';

import SearchQuery          from './SearchQuery';


const debounceFn =          fn => debounce( fn, 400 );


export default withDebouncedProps([ 'searchQuery' ], debounceFn )( SearchQuery );

