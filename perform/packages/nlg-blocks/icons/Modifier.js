import { h }                from 'preact';

export default ({ color, text = 'a.' }) =>
    <svg viewBox="0 0 36 30">
        <g transform="translate( 8, 0 )">
            <path
                d="m 0,0 H 28 V 7.5 c 0,10 -8,-8 -8,7.5 s 8,-2.5 8,7.5 V 30 H 0 V 22.5 c 0,-10 -8,8 -8,-7.5 s 8,2.5 8,-7.5 z"
                fill={ color }
            />
            <text
                children={ text }
                fill="#ffffff"
                font-weight="bold"
                transform="translate( 8, 18 )"
            />
        </g>
    </svg>;
