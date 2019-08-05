import { h }                from 'preact';

export default ({ color, text }) =>
    <svg viewBox="0 0 36 30">
        <path
            d="m0.034902 7.6191c0.0078967-4.2042 3.4122-7.797 7.6123-7.6123h2.6608l5.7092 3.8062h2.8546l5.7092-3.8062h11.419v26.264l-11.93-0.0698-5.7092 3.8062h-2.8546l-5.7092-3.8062h-9.7974z"
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
