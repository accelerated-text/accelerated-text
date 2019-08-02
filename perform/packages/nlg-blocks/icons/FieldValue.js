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
            ry="3"
            rx="3"
            y="7"
            x="12"
            height="16"
            width="21"
            fill="#ffffff"
            style="fill-opacity:0.51396652;stroke:none;stroke-width:1;stroke-opacity:0.50196078"
        />
    </svg>;
