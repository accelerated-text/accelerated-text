import { h }                from 'preact';

export default ({ color, text }) =>
    <svg viewBox="0 0 36 30">
        <g transform="translate(8)">
            <path
                d="m0 0h28v30h-28v-7.5c0-10-8 8-8-7.5s8 2.5 8-7.5z"
                fill={ color }
            />
        </g>
        <g transform="matrix(.62 0 0 .62 18 5.9)">
            <path
                d="m0 0 15-0.02v30l-15-0.099v-7.5c0-10-8 8-8-7.5s8 2.5 8-7.5z"
                fill="#fff"
            />
        </g>
        { text &&
            <text
                children={ text }
                fill="#ffffff"
                font-weight="bold"
                transform="translate( 8, 19 )"
            />
        }
    </svg>;
