import { h }                from 'preact';

export default ({ color, text }) =>
    <svg viewBox="0 0 110 50">
        <path
            d="m0 11c30-15 70-15 100 0h10v31h-60l-6 4h-3l-6-4h-7c-4.4 0-8 8-8 8l-20 0.15z"
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
    </svg>;
