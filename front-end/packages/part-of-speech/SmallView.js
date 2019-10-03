import { h }                from 'preact';

import toString             from './to-string';


export default ({ partOfSpeech, ...props }) =>
    <small
        { ...props }
        children={ toString( partOfSpeech ) }
    />;
