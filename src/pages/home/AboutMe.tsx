import { useMemo } from "react";

const myBirthday = new Date("2001-07-05");

export function AboutMe() {
	const age = useMemo(() => {
		const now = new Date();
		const diff = now.getTime() - myBirthday.getTime();
		return Math.floor(diff / (1000 * 60 * 60 * 24 * 365));
	}, []);

	return (
		<div className="min-w-40">
			<div className="font-semibold text-lg">About me</div>
			<div>👨 {age} years old</div>
			<div>👨‍💻 Software Engineer</div>
			<div>🇻🇳 HCMC, Vietnam</div>
			<div>🌻 Love sunflowers</div>
		</div>
	);
}
