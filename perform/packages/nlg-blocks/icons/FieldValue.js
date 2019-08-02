import { h }                from 'preact';


export default ({ color }) =>
    <svg viewBox="0 0 36 30">
        <g transform="translate(8)">
            <path
                d="M 0,0 H 28 V 30 H 0 v -7.5 c 0,-10 -8,8 -8,-7.5 0,-15.5 8,2.5 8,-7.5 z"
                fill={ color }
            />
        </g>
        <rect
            fill="#ffffff"
            fill-opacity=".5"
            height="16"
            rx="3"
            ry="3"
            width="21"
            x="12"
            y="7"
        />
    </svg>;
