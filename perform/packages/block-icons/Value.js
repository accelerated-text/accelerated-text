import { h }                from 'preact';


export default ({ color, height = 30, width = 36, ...props }) =>
    <svg height={ height } width={ width } { ...props }>
        <g transform="translate( 8, 0 )">
            <path
                d={ `m 0,0 H ${ width } v ${ height } H 0 V ${ height / 2 + 7.5 } c 0,-10 -8,8 -8,-7.5 s 8,2.5 8,-7.5 z` }
                fill={ color }
            />
        </g>
    </svg>;
