import React from 'react';
import { cn } from '../utils/cn';

const Alert = React.forwardRef(({ className, variant = 'default', children, ...props }, ref) => {
  const baseClasses = "relative w-full rounded-lg border p-4";
  
  const variants = {
    default: "bg-white text-gray-950 border-gray-200",
    destructive: "border-red-200 bg-red-50 text-red-900",
  };

  return (
    <div
      ref={ref}
      role="alert"
      className={cn(baseClasses, variants[variant], className)}
      {...props}
    >
      {children}
    </div>
  );
});

const AlertDescription = React.forwardRef(({ className, children, ...props }, ref) => (
  <div
    ref={ref}
    className={cn("text-sm", className)}
    {...props}
  >
    {children}
  </div>
));

Alert.displayName = "Alert";
AlertDescription.displayName = "AlertDescription";

export { Alert, AlertDescription };
