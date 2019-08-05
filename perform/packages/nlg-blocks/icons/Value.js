import { h }                from 'preact';

export default ({ color, text }) =>
    <svg viewBox="0 0 36 30">
        <g transform="translate(8)">
            <path
                d="M 0,0 H 28 V 30 H 0 v -7.5 c 0,-10 -8,8 -8,-7.5 0,-15.5 8,2.5 8,-7.5 z"
                fill={ color }
            />
            { text &&
                <text
                    children={ text }
                    fill="#ffffff"
                    font-weight="bold"
                    transform="translate( 8, 19 )"
                />
            }
        </g>
    </svg>;
