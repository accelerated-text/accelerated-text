import { h }                from 'preact';

import CURVE                from './curve';


const REVERSE_CURVE =       'c 0,5 -6,-4 -6,4 s 6,-1 6,4';


export default ({ color, ...props }) =>
    <svg viewBox="0 0 36 30" { ...props }>
        <g transform="translate( 8, 0 )">
            <path
                d={ `m 0,0 H 28 V 5 ${ REVERSE_CURVE } v 4 ${ REVERSE_CURVE } V 30 H 0 V 22.5 ${ CURVE } z` }
                fill={ color }
            />
        </g>
    </svg>;
